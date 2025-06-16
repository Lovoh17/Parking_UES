import threading
import time
from datetime import datetime
import logging
from .serial_connector import ArduinoConnector
from .database import LocalDatabase
from .firebase_handler import FirebaseHandler

class ParkingSystem:
    """Sistema principal de gestión de parqueo"""
    
    def __init__(self, config_file='config.json'):
        self.logger = logging.getLogger(__name__)
        self.config = self._load_config(config_file)
        self.system_active = False
        
        # Inicializar componentes
        self.serial_conn = ArduinoConnector(
            baudrate=self.config.get('baudrate', 9600),
            timeout=self.config.get('timeout', 1)
        )
        
        self.db = LocalDatabase(
            db_path=self.config.get('local_db_path', 'parking.db')
        )
        
        self.firebase = FirebaseHandler(
            credential_file=self.config['firebase_credentials'],
            database_url=self.config['firebase_db_url']
        )
        
        # Hilo de monitoreo
        self.monitor_thread = threading.Thread(target=self._monitor_hardware)
        self.monitor_thread.daemon = True
    
    def _load_config(self, config_file):
        """Carga la configuración desde JSON"""
        try:
            with open(config_file) as f:
                return json.load(f)
        except Exception as e:
            self.logger.error(f"Error al cargar configuración: {e}")
            raise
    
    def _monitor_hardware(self):
        """Hilo principal para monitorear el hardware"""
        self.system_active = True
        while self.system_active:
            data = self.serial_conn.read_json_data()
            if data:
                self._process_hardware_data(data)
            
            time.sleep(0.1)
    
    def _process_hardware_data(self, data):
        """Procesa los datos JSON recibidos del hardware"""
        try:
            event_type = data.get('event')
            
            if event_type == 'vehicle_entry':
                self._process_vehicle_entry(data)
            elif event_type == 'vehicle_exit':
                self._process_vehicle_exit(data)
            elif event_type == 'sensor_data':
                self._process_sensor_data(data)
            else:
                self.logger.warning(f"Tipo de evento desconocido: {event_type}")
                
        except Exception as e:
            self.logger.error(f"Error procesando datos del hardware: {e}")
    
    # ... (implementar métodos de procesamiento)
    
    def start_system(self):
        """Inicia el sistema"""
        if not all([
            self.serial_conn.connect(self.config.get('port')),
            self.db.connect(),
            self.firebase.initialize()
        ]):
            raise ConnectionError("No se pudieron inicializar todos los componentes")
        
        if not self.monitor_thread.is_alive():
            self.monitor_thread.start()
            self.logger.info("Sistema de parqueo iniciado")
    
    def stop_system(self):
        """Detiene el sistema"""
        self.system_active = False
        self.serial_conn.disconnect()
        self.db.close()
        self.logger.info("Sistema de parqueo detenido")
    
    def __del__(self):
        self.stop_system()