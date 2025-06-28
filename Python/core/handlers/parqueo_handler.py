from datetime import datetime
import firebase_admin
from firebase_admin import db, credentials
from .base_handler import BaseMessageHandler

class ParqueoHandler(BaseMessageHandler):
    """Handler para gestión de asignación de parqueos y registro en Firebase"""
    
    def __init__(self, max_parqueos=8):
        super().__init__("Parqueo")
        self.firebase_url = "https://parkingues-69cfa-default-rtdb.firebaseio.com"
        self.max_parqueos = max_parqueos
        self.parqueos_disponibles = list(range(1, max_parqueos + 1))  # [1, 2, 3, ..., 8]
        self.parqueos_ocupados = {}  # {parqueo_num: user_id}
        
        # Inicializar Firebase si no está inicializado
        if not firebase_admin._apps:
            try:
                cred = credentials.Certificate("serviceAccountKey.json")
                firebase_admin.initialize_app(cred, {
                    'databaseURL': self.firebase_url
                })
                self.firebase_enabled = True
            except Exception as e:
                self.logger.error(f"Error inicializando Firebase: {e}")
                self.firebase_enabled = False
        else:
            self.firebase_enabled = True
            
        # Referencias Firebase
        if self.firebase_enabled:
            self.parqueos_ref = db.reference('/parqueos')
            self.historial_ref = db.reference('/historial_accesos')
        
        self.logger.info("ParqueoHandler inicializado correctamente")

    def handle(self, data, timestamp):
        """Procesa mensajes relacionados con parqueos"""
        if "AccesoVehiculo" in data and "NumeroParqueo" in data["AccesoVehiculo"]:
            return self._procesar_llegada_parqueo(data["AccesoVehiculo"]["NumeroParqueo"], timestamp)
        return None

    def _procesar_llegada_parqueo(self, numero_parqueo, timestamp):
        """Procesa la llegada de un vehículo a un parqueo específico"""
        try:
            parqueo_num = int(numero_parqueo)
            if parqueo_num < 1 or parqueo_num > self.max_parqueos:
                raise ValueError("Número de parqueo inválido")
                
            # Verificar si el parqueo está ocupado por un usuario
            user_id = self.parqueos_ocupados.get(parqueo_num)
            
            if user_id:
                # Registrar el acceso en Firebase
                registro = {
                    "user_id": user_id,
                    "parqueo": parqueo_num,
                    "timestamp": timestamp.isoformat(),
                    "evento": "llegada"
                }
                
                if self.firebase_enabled:
                    try:
                        # Guardar en historial
                        self.historial_ref.push().set(registro)
                        # Actualizar estado actual
                        self.parqueos_ref.child(str(parqueo_num)).set({
                            "ocupado": True,
                            "user_id": user_id,
                            "ultima_actualizacion": timestamp.isoformat()
                        })
                        
                        self.logger.info(f"📌 Registrada llegada al parqueo {parqueo_num} por usuario {user_id}")
                        
                        # Liberar el parqueo de los ocupados
                        self.parqueos_ocupados.pop(parqueo_num, None)
                        # Añadir a disponibles (si no está ya)
                        if parqueo_num not in self.parqueos_disponibles:
                            self.parqueos_disponibles.append(parqueo_num)
                            
                    except Exception as e:
                        self.logger.error(f"Error al actualizar Firebase: {e}")
                
                resultado = {
                    "handler": self.name,
                    "evento": "llegada_parqueo",
                    "user_id": user_id,
                    "parqueo_num": parqueo_num,
                    "timestamp": timestamp,
                    "mensaje": f"Usuario {user_id} estacionado en parqueo {parqueo_num}"
                }
                
                self.notify_callbacks(resultado)
                return resultado
            
            else:
                self.logger.warning(f"Parqueo {parqueo_num} ocupado sin asignación previa")
                return None
                
        except Exception as e:
            self.logger.error(f"Error procesando llegada a parqueo: {e}")
            return None

    def asignar_parqueo(self, user_id):
        """Asigna un parqueo disponible a un usuario"""
        if not self.parqueos_disponibles:
            self.logger.warning("No hay parqueos disponibles para asignar")
            return None
            
        parqueo_num = self.parqueos_disponibles.pop(0)  # Toma el primer disponible
        self.parqueos_ocupados[parqueo_num] = user_id
        
        self.logger.info(f"🅿️ Asignado parqueo {parqueo_num} al usuario {user_id}")
        
        # Registrar asignación en Firebase
        if self.firebase_enabled:
            try:
                registro = {
                    "user_id": user_id,
                    "parqueo": parqueo_num,
                    "timestamp": datetime.now().isoformat(),
                    "evento": "asignacion"
                }
                self.historial_ref.push().set(registro)
            except Exception as e:
                self.logger.error(f"Error al registrar asignación en Firebase: {e}")
        
        return parqueo_num

    def liberar_parqueo(self, parqueo_num):
        """Libera un parqueo y lo marca como disponible"""
        try:
            parqueo_num = int(parqueo_num)
            if parqueo_num in self.parqueos_ocupados:
                user_id = self.parqueos_ocupados.pop(parqueo_num)
                if parqueo_num not in self.parqueos_disponibles:
                    self.parqueos_disponibles.append(parqueo_num)
                    self.parqueos_disponibles.sort()  # Mantener ordenados
                
                self.logger.info(f"🆓 Parqueo {parqueo_num} liberado por usuario {user_id}")
                
                # Registrar liberación en Firebase
                if self.firebase_enabled:
                    try:
                        registro = {
                            "user_id": user_id,
                            "parqueo": parqueo_num,
                            "timestamp": datetime.now().isoformat(),
                            "evento": "salida"
                        }
                        self.historial_ref.push().set(registro)
                        
                        # Actualizar estado actual
                        self.parqueos_ref.child(str(parqueo_num)).set({
                            "ocupado": False,
                            "user_id": None,
                            "ultima_actualizacion": datetime.now().isoformat()
                        })
                    except Exception as e:
                        self.logger.error(f"Error al registrar liberación en Firebase: {e}")
                
                return True
            return False
        except Exception as e:
            self.logger.error(f"Error liberando parqueo: {e}")
            return False

    def get_estado_parqueos(self):
        """Retorna el estado actual de los parqueos"""
        return {
            "disponibles": self.parqueos_disponibles.copy(),
            "ocupados": self.parqueos_ocupados.copy()
        }