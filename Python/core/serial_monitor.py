import json
import time
import logging
import threading
from datetime import datetime
from lib.arduino_connector import ArduinoConnector

class SerialMonitor:
    """Monitor serial mejorado con sistema de switch case para handlers"""
    
    def __init__(self, baudrate=9600, timeout=1):
        self.connector = ArduinoConnector(baudrate, timeout)
        self.logger = logging.getLogger(__name__)
        self.monitoring = False
        self.monitor_thread = None
        self.json_mode = True  # True para JSON, False para texto plano
        self.message_queue = []
        self.queue_lock = threading.Lock()
        
        # Sistema de handlers por tipo de mensaje
        self.message_handlers = {}
        self.default_handler = None
        self.handler_stats = {}
        
        # Configuración para depuración
        self.debug_mode = False
        self.log_raw_data = False
        
        # Estadísticas
        self.stats = {
            "messages_received": 0,
            "messages_processed": 0,
            "messages_handled": 0,
            "messages_unhandled": 0,
            "json_errors": 0,
            "connection_errors": 0,
            "handler_errors": 0,
            "last_message_time": None,
            "start_time": None
        }
        
        self.logger.info("SerialMonitor con sistema de handlers inicializado")

    def register_handler(self, message_type, handler):
        """Registra un handler para un tipo específico de mensaje"""
        if not hasattr(handler, 'handle'):
            raise ValueError("El handler debe tener un método 'handle'")
            
        self.message_handlers[message_type] = handler
        self.handler_stats[message_type] = {
            "messages_processed": 0,
            "last_processed": None,
            "errors": 0
        }
        
        self.logger.info(f"Handler registrado para '{message_type}': {handler.__class__.__name__}")
        return True

    def unregister_handler(self, message_type):
        """Desregistra un handler específico"""
        if message_type in self.message_handlers:
            handler = self.message_handlers.pop(message_type)
            self.handler_stats.pop(message_type, None)
            self.logger.info(f"Handler desregistrado para '{message_type}': {handler.__class__.__name__}")
            return True
        return False

    def set_default_handler(self, handler):
        """Establece un handler por defecto para mensajes no reconocidos"""
        if handler and not hasattr(handler, 'handle'):
            raise ValueError("El handler por defecto debe tener un método 'handle'")
            
        self.default_handler = handler
        self.logger.info(f"Handler por defecto establecido: {handler.__class__.__name__ if handler else 'None'}")

    def get_registered_handlers(self):
        """Obtiene lista de handlers registrados"""
        return {
            message_type: handler.__class__.__name__ 
            for message_type, handler in self.message_handlers.items()
        }

    def set_json_mode(self, json_mode=True):
        """Configura si esperar datos en formato JSON o texto plano"""
        self.json_mode = json_mode
        self.logger.info(f"Modo configurado: {'JSON' if json_mode else 'Texto plano'}")
    
    def set_debug_mode(self, enabled=True):
        """Habilita/deshabilita modo debug"""
        self.debug_mode = enabled
        self.log_raw_data = enabled
        self.logger.info(f"Modo debug: {'Habilitado' if enabled else 'Deshabilitado'}")

    def _extract_message_type(self, data):
        """Extrae el tipo de mensaje de los datos recibidos"""
        if not isinstance(data, dict):
            return None
            
        # Busca el primer key que no sea metadata
        metadata_keys = {'timestamp', 'id', 'source', 'priority'}
        
        for key in data.keys():
            if key not in metadata_keys:
                return key
                
        return None

    def _route_message_to_handler(self, data, timestamp):
        """Rutea el mensaje al handler apropiado usando switch case logic"""
        message_type = self._extract_message_type(data)
        
        if self.debug_mode:
            self.logger.debug(f"Tipo de mensaje detectado: {message_type}")
            
        # Switch case implementation
        handler = None
        handler_name = "unknown"
        
        if message_type and message_type in self.message_handlers:
            # Caso: Handler específico registrado
            handler = self.message_handlers[message_type]
            handler_name = f"{message_type}_handler"
            
        elif self.default_handler:
            # Caso: Handler por defecto
            handler = self.default_handler
            handler_name = "default_handler"
            message_type = "default"
            
        else:
            # Caso: Sin handler disponible
            self.stats["messages_unhandled"] += 1
            self.logger.warning(f"No hay handler para mensaje tipo '{message_type}': {data}")
            return None

        # Ejecutar handler seleccionado
        try:
            if self.debug_mode:
                self.logger.debug(f"Ejecutando {handler_name} para mensaje tipo '{message_type}'")
                
            # Llamar al handler con los datos
            if hasattr(handler, 'handle'):
                result = handler.handle(data, timestamp)
            else:
                # Fallback para handlers que no siguen el patrón estándar
                result = handler(data, timestamp)
                
            # Actualizar estadísticas del handler
            if message_type in self.handler_stats:
                self.handler_stats[message_type]["messages_processed"] += 1
                self.handler_stats[message_type]["last_processed"] = timestamp
                
            self.stats["messages_handled"] += 1
            
            if self.debug_mode and result:
                self.logger.debug(f"Resultado del handler: {result}")
                
            return result
            
        except Exception as e:
            self.logger.error(f"Error en {handler_name}: {e}")
            self.stats["handler_errors"] += 1
            
            if message_type in self.handler_stats:
                self.handler_stats[message_type]["errors"] += 1
                
            if self.debug_mode:
                import traceback
                self.logger.debug(f"Traceback: {traceback.format_exc()}")
                
            return None

    def read_json_data(self):
        """Lee y parsea datos JSON del Arduino"""
        ser = self.connector.get_serial_object()
        if not ser or ser.in_waiting == 0:
            return None
            
        try:
            line = ser.readline().decode('utf-8').strip()
            if line:
                if self.log_raw_data:
                    self.logger.debug(f"Raw data: {line}")
                
                # Intenta parsear JSON
                parsed_data = json.loads(line)
                self.stats["messages_received"] += 1
                return parsed_data
            
        except json.JSONDecodeError as e:
            self.stats["json_errors"] += 1
            self.logger.error(f"Error parsing JSON: {e} - Línea: {line}")
            
            # En modo debug, intenta recuperar información útil
            if self.debug_mode:
                self._handle_malformed_json(line, e)
            
            return None
            
        except UnicodeDecodeError as e:
            self.stats["connection_errors"] += 1
            self.logger.error(f"Error decodificando Unicode: {e}")
            return None
            
        except Exception as e:
            self.stats["connection_errors"] += 1
            self.logger.error(f"Error leyendo serial: {e}")
            return None
    
    def _handle_malformed_json(self, line, error):
        """Maneja JSON malformado en modo debug"""
        self.logger.debug(f"Intentando recuperar de JSON malformado: {line}")
        
        # Intenta detectar patrones comunes
        if line.startswith('{') and not line.endswith('}'):
            self.logger.debug("JSON parece incompleto - posible fragmentación")
        elif '"' in line:
            self.logger.debug("JSON contiene comillas - posible error de escape")
    
    def read_text_data(self):
        """Lee datos de texto plano del Arduino"""
        ser = self.connector.get_serial_object()
        if not ser or ser.in_waiting == 0:
            return None
            
        try:
            line = ser.readline().decode('utf-8').strip()
            if line:
                self.stats["messages_received"] += 1
                if self.log_raw_data:
                    self.logger.debug(f"Raw text: {line}")
                return line
            return None
            
        except UnicodeDecodeError as e:
            self.stats["connection_errors"] += 1
            self.logger.error(f"Error decodificando Unicode: {e}")
            return None
            
        except Exception as e:
            self.stats["connection_errors"] += 1
            self.logger.error(f"Error leyendo serial: {e}")
            return None

    def send_command(self, command, data=None):
        """Envía un comando al Arduino"""
        ser = self.connector.get_serial_object()
        if not ser:
            self.logger.warning("No hay conexión con el Arduino")
            return False
            
        try:
            if self.json_mode:
                if isinstance(command, dict):
                    # Si command ya es un dict (respuesta completa), lo envía directamente
                    message = command
                else:
                    # Formato tradicional con command y data
                    message = {"command": command}
                    if data:
                        if isinstance(data, dict):
                            message.update(data)
                        else:
                            message["data"] = data
                
                json_str = json.dumps(message)
                ser.write((json_str + '\n').encode())
                
                if self.debug_mode:
                    self.logger.debug(f"Enviado: {json_str}")
                    
            else:
                # Modo texto plano
                if isinstance(command, dict):
                    message = json.dumps(command) + '\n'
                else:
                    message = f"{command}\n"
                ser.write(message.encode())
                
                if self.debug_mode:
                    self.logger.debug(f"Enviado (texto): {message.strip()}")
            
            return True
            
        except Exception as e:
            self.logger.error(f"Error al enviar comando: {e}")
            return False

    def send_response(self, response_data):
        """Envía una respuesta directamente"""
        return self.send_command(response_data)

    def _process_message(self, data, timestamp):
        """Procesa un mensaje y lo rutea al handler apropiado"""
        self.stats["messages_processed"] += 1
        self.stats["last_message_time"] = timestamp
        
        # Añade el mensaje a la cola para procesamiento asíncrono si es necesario
        with self.queue_lock:
            self.message_queue.append({
                "data": data,
                "timestamp": timestamp,
                "processed": False
            })
        
        # Rutea el mensaje al handler apropiado
        handler_result = self._route_message_to_handler(data, timestamp)
        
        # Si el handler retorna una respuesta, la envía
        if handler_result and isinstance(handler_result, dict):
            if 'response' in handler_result or any(key.startswith('Respuesta') for key in handler_result.keys()):
                self.send_response(handler_result)
        
        # Marca el mensaje como procesado
        with self.queue_lock:
            if self.message_queue:
                self.message_queue[-1]["processed"] = True
                self.message_queue[-1]["handler_result"] = handler_result
        
        return handler_result

    def _monitor_loop(self):
        """Loop principal del monitor (ejecuta en hilo separado)"""
        self.logger.info("Iniciando monitoreo serial con sistema de handlers...")
        self.stats["start_time"] = datetime.now()
        
        while self.monitoring:
            try:
                # Verifica conexión
                if not self.connector.is_connected():
                    self.logger.warning("Conexión perdida, intentando reconectar...")
                    if not self.connector.reconnect():
                        time.sleep(1)
                        continue
                
                # Lee datos según el modo configurado
                data = None
                if self.json_mode:
                    data = self.read_json_data()
                else:
                    data = self.read_text_data()
                
                if data is not None:
                    # Genera timestamp
                    timestamp = datetime.now().strftime("%Y-%m-%d %H:%M:%S.%f")[:-3]
                    
                    # Procesa el mensaje
                    self._process_message(data, timestamp)
                
                # Pausa pequeña para no saturar CPU
                time.sleep(0.005)  # 5ms
                
            except Exception as e:
                self.logger.error(f"Error en loop de monitoreo: {e}")
                if self.debug_mode:
                    import traceback
                    self.logger.debug(f"Traceback: {traceback.format_exc()}")
                time.sleep(0.1)

    def start_monitoring(self, port_name=None):
        """Inicia el monitoreo del puerto serial"""
        if self.monitoring:
            self.logger.warning("El monitoreo ya está activo")
            return False
        
        # Conecta al Arduino
        self.logger.info(f"Intentando conectar al puerto: {port_name or 'auto-detectar'}")
        if not self.connector.connect(port_name):
            self.logger.error("No se pudo establecer conexión con Arduino")
            return False
        
        # Reinicia estadísticas
        self.stats["messages_received"] = 0
        self.stats["messages_processed"] = 0
        self.stats["messages_handled"] = 0
        self.stats["messages_unhandled"] = 0
        self.stats["json_errors"] = 0
        self.stats["connection_errors"] = 0
        self.stats["handler_errors"] = 0
        
        # Reinicia estadísticas de handlers
        for message_type in self.handler_stats:
            self.handler_stats[message_type]["messages_processed"] = 0
            self.handler_stats[message_type]["errors"] = 0
        
        # Inicia el hilo de monitoreo
        self.monitoring = True
        self.monitor_thread = threading.Thread(target=self._monitor_loop, daemon=True)
        self.monitor_thread.start()
        
        self.logger.info(f"Monitoreo serial iniciado en {self.connector.get_port_name()}")
        self.logger.info(f"Handlers registrados: {len(self.message_handlers)}")
        self.logger.info(f"Modo: {'JSON' if self.json_mode else 'Texto'}, Debug: {self.debug_mode}")
        return True
    
    def stop_monitoring(self):
        """Detiene el monitoreo"""
        if self.monitoring:
            self.logger.info("Deteniendo monitoreo serial...")
            self.monitoring = False
            
            if self.monitor_thread and self.monitor_thread.is_alive():
                self.monitor_thread.join(timeout=2)
                if self.monitor_thread.is_alive():
                    self.logger.warning("El hilo de monitoreo no se detuvo correctamente")
            
            self.connector.disconnect()
            self._print_final_stats()
            self.logger.info("Monitoreo serial detenido")

    def _print_final_stats(self):
        """Imprime estadísticas finales"""
        if self.stats["start_time"]:
            duration = datetime.now() - self.stats["start_time"]
            self.logger.info(f"Estadísticas de sesión ({duration.total_seconds():.1f}s):")
            self.logger.info(f"  Mensajes recibidos: {self.stats['messages_received']}")
            self.logger.info(f"  Mensajes procesados: {self.stats['messages_processed']}")
            self.logger.info(f"  Mensajes manejados: {self.stats['messages_handled']}")
            self.logger.info(f"  Mensajes sin handler: {self.stats['messages_unhandled']}")
            self.logger.info(f"  Errores JSON: {self.stats['json_errors']}")
            self.logger.info(f"  Errores conexión: {self.stats['connection_errors']}")
            self.logger.info(f"  Errores handlers: {self.stats['handler_errors']}")
            
            # Estadísticas por handler
            if self.handler_stats:
                self.logger.info("  Estadísticas por handler:")
                for message_type, stats in self.handler_stats.items():
                    self.logger.info(f"    {message_type}: {stats['messages_processed']} procesados, {stats['errors']} errores")

    def is_monitoring(self):
        """Verifica si el monitoreo está activo"""
        return self.monitoring and self.connector.is_connected()
    
    def get_connection_info(self):
        """Obtiene información de la conexión actual"""
        if self.connector.is_connected():
            return {
                "connected": True,
                "port": self.connector.get_port_name(),
                "baudrate": self.connector.baudrate,
                "timeout": self.connector.timeout
            }
        return {"connected": False}
    
    def get_stats(self):
        """Obtiene estadísticas actuales"""
        stats = self.stats.copy()
        if stats["start_time"]:
            stats["uptime_seconds"] = (datetime.now() - stats["start_time"]).total_seconds()
        
        # Incluye estadísticas de handlers
        stats["handler_stats"] = self.handler_stats.copy()
        stats["registered_handlers"] = self.get_registered_handlers()
        
        return stats
    
    def get_message_queue_size(self):
        """Obtiene el tamaño actual de la cola de mensajes"""
        with self.queue_lock:
            return len(self.message_queue)
    
    def clear_message_queue(self):
        """Limpia la cola de mensajes"""
        with self.queue_lock:
            cleared = len(self.message_queue)
            self.message_queue.clear()
        self.logger.info(f"Cola de mensajes limpiada: {cleared} mensajes removidos")
        return cleared

    def __del__(self):
        self.stop_monitoring()


# Ejemplo de uso con sistema de handlers
if __name__ == "__main__":
    # Configura logging
    logging.basicConfig(
        level=logging.INFO,
        format='%(asctime)s - %(name)s - %(levelname)s - %(message)s'
    )
    
    # Crea el monitor
    monitor = SerialMonitor(baudrate=9600)
    monitor.set_debug_mode(True)
    
    # Importa y registra handlers
    try:
        from Python.core.handlers.acceso_vehiculo_handler import AccesoVehiculoHandler
        from Python.core.handlers.parqueo_handler import ParqueoHandler
        
        # Instancia handlers
        acceso_handler = AccesoVehiculoHandler()
        parqueo_handler = ParqueoHandler()
        
        # Registra handlers por tipo de mensaje
        monitor.register_handler("AccesoVehiculo", acceso_handler)
        monitor.register_handler("AsignacionParqueo", parqueo_handler)
        monitor.register_handler("NumeroParqueo", parqueo_handler)
        
        # Handler por defecto para mensajes no reconocidos
        def default_message_handler(data, timestamp):
            print(f"[{timestamp}] Mensaje no reconocido: {data}")
            return {"error": "Tipo de mensaje no soportado", "data": data}
        
        monitor.set_default_handler(default_message_handler)
        
    except ImportError as e:
        print(f"Error importando handlers: {e}")
        print("Usando handlers de ejemplo...")
        
        # Handlers de ejemplo si no se pueden importar los reales
        class ExampleHandler:
            def __init__(self, name):
                self.name = name
                
            def handle(self, data, timestamp):
                print(f"[{self.name}] Procesando: {data}")
                return {"handler": self.name, "processed": True, "timestamp": timestamp}
        
        monitor.register_handler("AccesoVehiculo", ExampleHandler("AccesoVehiculo"))
        monitor.register_handler("NumeroParqueo", ExampleHandler("Parqueo"))
    
    # Función para mostrar estadísticas
    def print_stats():
        stats = monitor.get_stats()
        print(f"\n📊 Estadísticas del Monitor:")
        print(f"  Mensajes recibidos: {stats['messages_received']}")
        print(f"  Mensajes procesados: {stats['messages_processed']}")
        print(f"  Mensajes manejados: {stats['messages_handled']}")
        print(f"  Mensajes sin handler: {stats['messages_unhandled']}")
        print(f"  Errores JSON: {stats['json_errors']}")
        print(f"  Errores handlers: {stats['handler_errors']}")
        print(f"  Último mensaje: {stats['last_message_time'] or 'Ninguno'}")
        
        if 'uptime_seconds' in stats:
            print(f"  Tiempo activo: {stats['uptime_seconds']:.1f}s")
        print(f"  Cola de mensajes: {monitor.get_message_queue_size()}")
        
        # Estadísticas por handler
        if stats.get('handler_stats'):
            print(f"\n📋 Estadísticas por Handler:")
            for msg_type, handler_stats in stats['handler_stats'].items():
                print(f"  {msg_type}: {handler_stats['messages_processed']} procesados, {handler_stats['errors']} errores")
        
        # Handlers registrados
        if stats.get('registered_handlers'):
            print(f"\n🔧 Handlers Registrados:")
            for msg_type, handler_name in stats['registered_handlers'].items():
                print(f"  {msg_type} -> {handler_name}")
    
    try:
        # Configura modo JSON
        monitor.set_json_mode(True)
        
        # Inicia monitoreo
        if monitor.start_monitoring():
            print("🔧 SerialMonitor con Sistema de Handlers iniciado")
            print("📋 Routing automático por tipo de mensaje")
            print("🔍 Modo debug habilitado")
            print(f"📨 Handlers registrados: {len(monitor.get_registered_handlers())}")
            print("\n--- Comandos disponibles ---")
            print("stats - Ver estadísticas")
            print("handlers - Ver handlers registrados")
            print("clear - Limpiar cola de mensajes")
            print("info - Info de conexión")
            print("test - Enviar mensaje de prueba")
            print("quit - Salir")
            
            while True:
                command = input("\n> ").strip().lower()
                
                if command == "quit":
                    break
                elif command == "stats":
                    print_stats()
                elif command == "handlers":
                    handlers = monitor.get_registered_handlers()
                    print(f"\n🔧 Handlers Registrados ({len(handlers)}):")
                    for msg_type, handler_name in handlers.items():
                        print(f"  {msg_type} -> {handler_name}")
                elif command == "clear":
                    cleared = monitor.clear_message_queue()
                    print(f"✅ {cleared} mensajes limpiados de la cola")
                elif command == "info":
                    info = monitor.get_connection_info()
                    print(f"🔌 Conexión: {info}")
                elif command == "test":
                    # Envía mensajes de prueba para diferentes handlers
                    test_messages = [
                        {"AccesoVehiculo": {"Codigo": "1234"}},
                        {"NumeroParqueo": {"Parqueo": 3}},
                        {"MensajeDesconocido": {"test": "data"}}
                    ]
                    
                    for msg in test_messages:
                        if monitor.send_command(msg):
                            print(f"✅ Mensaje enviado: {msg}")
                        else:
                            print(f"❌ Error enviando: {msg}")
                else:
                    print("Comando no reconocido")
        else:
            print("❌ Error al iniciar el monitor")
            
    except KeyboardInterrupt:
        print("\n🛑 Deteniendo monitor...")
    finally:
        monitor.stop_monitoring()
        print("👋 Monitor detenido")