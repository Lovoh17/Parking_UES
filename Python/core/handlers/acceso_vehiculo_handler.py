# acceso_vehiculo_handler.py
import threading
from datetime import datetime
from firebase_admin import db
from .base_handler import BaseMessageHandler
from ..lib.firebase_manager import init_firebase

class AccesoVehiculoHandler(BaseMessageHandler):
    """Handler para mensajes de AccesoVehiculo con integración Firebase"""
    
    def __init__(self, codigo_acceso_valido="1111"):
        super().__init__("AccesoVehiculo")
        self.codigo_acceso_valido = codigo_acceso_valido
        self.firebase_enabled = init_firebase()
        
        self.estadisticas = {
            "accesos_permitidos": 0,
            "accesos_denegados": 0,
            "total_solicitudes": 0,
            "ultimo_acceso": None
        }
        
        self.current_firebase_code = None
        self.current_status = None
        self.current_user_id = None
        
        if self.firebase_enabled:
            try:
                self.codigo_ref = db.reference('/codigo_acceso')
                self._iniciar_listener_firebase()
                self.logger.info("Configuración Firebase completada")
            except Exception as e:
                self.logger.error(f"Error configurando Firebase: {e}")
                self.firebase_enabled = False

    def _iniciar_listener_firebase(self):
        """Inicia listener para cambios en Firebase en un hilo separado"""
        def listener(event):
            try:
                data = event.data
                if data:
                    self.current_firebase_code = data.get('codigo')
                    self.current_status = data.get('status')
                    self.current_user_id = data.get('user_id')
                    self.logger.debug("Datos Firebase actualizados")
            except Exception as e:
                self.logger.error(f"Error en listener Firebase: {e}")

        try:
            threading.Thread(
                target=lambda: self.codigo_ref.listen(listener),
                daemon=True,
                name="FirebaseListener"
            ).start()
        except Exception as e:
            self.logger.error(f"No se pudo iniciar listener: {e}")

    def handle(self, data, timestamp):
        """Procesa mensajes de acceso vehicular"""
        if not isinstance(data, dict) or "Codigo" not in data:
            self.logger.warning("Mensaje sin formato válido")
            return None

        codigo = str(data["Codigo"])
        self._actualizar_estadisticas()
        
        if self.firebase_enabled:
            self._update_firebase_codigo(codigo)

        acceso_permitido = self._validar_acceso(codigo)
        response = self._generar_respuesta(codigo, timestamp, acceso_permitido)
        
        if acceso_permitido and self._debe_resetear_firebase(codigo):
            self._reset_firebase_status()

        self._notificar_resultado(codigo, timestamp, acceso_permitido, response)
        return response

    def _actualizar_estadisticas(self):
        """Actualiza las estadísticas de acceso"""
        self.estadisticas["total_solicitudes"] += 1
        self.estadisticas["ultimo_acceso"] = datetime.now()

    def _validar_acceso(self, codigo):
        """Valida el código de acceso"""
        # Verificación contra código local
        if codigo == self.codigo_acceso_valido:
            return True
            
        # Verificación contra Firebase
        if (self.firebase_enabled and 
            self.current_firebase_code and 
            str(self.current_firebase_code) == codigo and 
            self.current_status == "Permitido"):
            return True
            
        return False

    def _debe_resetear_firebase(self, codigo):
        """Determina si debe resetear estado en Firebase"""
        return (self.firebase_enabled and 
                self.current_status == "Permitido" and 
                str(self.current_firebase_code) == codigo)

    def _generar_respuesta(self, codigo, timestamp, permitido):
        """Genera respuesta según el resultado de validación"""
        if permitido:
            self.estadisticas["accesos_permitidos"] += 1
            self.logger.info(f"Acceso permitido para código: {codigo}")
            return self._crear_respuesta_permitida(codigo, timestamp)
        else:
            self.estadisticas["accesos_denegados"] += 1
            self.logger.warning(f"Acceso denegado para código: {codigo}")
            return self._crear_respuesta_denegada(codigo, timestamp)

    def _notificar_resultado(self, codigo, timestamp, permitido, response):
        """Notifica a los callbacks registrados"""
        resultado = {
            "handler": self.name,
            "codigo": codigo,
            "permitido": permitido,
            "timestamp": timestamp,
            "mensaje": "Acceso autorizado" if permitido else "Código incorrecto",
            "response": response,
            "user_id": self.current_user_id if permitido else None
        }
        self.notify_callbacks(resultado)

    def _update_firebase_codigo(self, codigo):
        """Actualiza el código en Firebase"""
        try:
            self.codigo_ref.update({'codigo': codigo})
            self.logger.debug(f"Código actualizado en Firebase: {codigo}")
            return True
        except Exception as e:
            self.logger.error(f"Error actualizando Firebase: {e}")
            return False

    def _reset_firebase_status(self):
        """Resetea el estado en Firebase"""
        try:
            self.codigo_ref.update({'status': None, 'user_id': None})
            self.logger.info("Estado Firebase reseteado")
        except Exception as e:
            self.logger.error(f"Error reseteando Firebase: {e}")

    def _crear_respuesta_permitida(self, codigo, timestamp):
        return {
            "RespuestaAcceso": {
                "Resultado": "PERMITIDO",
                "Codigo": codigo,
                "Mensaje": "Bienvenido",
                "Timestamp": timestamp,
                "ComandoBarrera": {
                    "Accion": "ABRIR",
                    "Duracion": 5000
                }
            }
        }

    def _crear_respuesta_denegada(self, codigo, timestamp):
        return {
            "RespuestaAcceso": {
                "Resultado": "DENEGADO",
                "Codigo": codigo,
                "Mensaje": "Código incorrecto",
                "Timestamp": timestamp
            }
        }

    def cambiar_codigo_acceso(self, nuevo_codigo):
        """Actualiza el código de acceso válido"""
        self.codigo_acceso_valido = nuevo_codigo
        self.logger.info(f"Código de acceso actualizado a: {nuevo_codigo}")

    def get_estadisticas(self):
        """Obtiene copia de las estadísticas actuales"""
        return self.estadisticas.copy()

    def reiniciar_estadisticas(self):
        """Reinicia todas las estadísticas"""
        self.estadisticas = {
            "accesos_permitidos": 0,
            "accesos_denegados": 0,
            "total_solicitudes": 0,
            "ultimo_acceso": None
        }
        self.logger.info("Estadísticas reiniciadas")