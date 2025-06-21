import firebase_admin
from firebase_admin import credentials, db
import logging
import json

class FirebaseHandler:
    """Maneja la comunicación con Firebase Realtime Database"""
    
    def __init__(self, credential_file, database_url):
        self.credential_file = credential_file
        self.database_url = database_url
        self.app = None
        self.logger = logging.getLogger(__name__)
        
    def initialize(self):
        """Inicializa la conexión con Firebase"""
        try:
            cred = credentials.Certificate(self.credential_file)
            self.app = firebase_admin.initialize_app(cred, {
                'databaseURL': self.database_url
            })
            self.logger.info("Conexión con Firebase establecida")
            return True
        except Exception as e:
            self.logger.error(f"Error al conectar con Firebase: {e}")
            return False
    
    def sync_vehicle(self, vehicle_data):
        """Sincroniza datos de un vehículo"""
        try:
            ref = db.reference('vehicles')
            ref.child(vehicle_data['plate_number']).set(vehicle_data)
            self.logger.debug(f"Vehículo sincronizado: {vehicle_data['plate_number']}")
            return True
        except Exception as e:
            self.logger.error(f"Error al sincronizar vehículo: {e}")
            return False
    
    # ... (otros métodos para sincronización)
    
    def __del__(self):
        if self.app:
            firebase_admin.delete_app(self.app)
            self.logger.info("Conexión con Firebase cerrada")