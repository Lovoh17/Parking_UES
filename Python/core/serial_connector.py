import serial
import serial.tools.list_ports
import json
import logging

class ArduinoConnector:
    """Maneja la comunicación serial con Arduino/ESP32 usando JSON"""
    
    def __init__(self, baudrate=9600, timeout=1):
        self.baudrate = baudrate
        self.timeout = timeout
        self.port = None
        self.ser = None
        self.connected = False
        self.logger = logging.getLogger(__name__)
        
    def find_arduino_port(self):
        """Autodetecta el puerto del Arduino"""
        ports = serial.tools.list_ports.comports()
        for port in ports:
            if 'Arduino' in port.description or 'CH340' in port.description:
                return port.device
        return None
    
    def connect(self, port_name=None):
        """Establece la conexión serial"""
        try:
            if port_name is None:
                port_name = self.find_arduino_port()
                if port_name is None:
                    self.logger.error("No se pudo detectar el puerto del Arduino")
                    return False
            
            self.port = port_name
            self.ser = serial.Serial(self.port, self.baudrate, timeout=self.timeout)
            self.connected = True
            self.logger.info(f"Conectado al Arduino en {self.port}")
            return True
            
        except Exception as e:
            self.logger.error(f"Error al conectar con el Arduino: {e}")
            self.connected = False
            return False
    
    def read_json_data(self):
        """Lee y parsea datos JSON del Arduino"""
        if not self.connected or self.ser.in_waiting == 0:
            return None
            
        try:
            line = self.ser.readline().decode('utf-8').strip()
            return json.loads(line)
        except (json.JSONDecodeError, UnicodeDecodeError) as e:
            self.logger.error(f"Error parsing JSON: {e}")
            return None
        except Exception as e:
            self.logger.error(f"Error reading serial: {e}")
            return None
    
    def send_command(self, command, data=None):
        """Envía un comando al Arduino en formato JSON"""
        if not self.connected:
            self.logger.warning("No hay conexión con el Arduino")
            return False
            
        try:
            message = {"command": command}
            if data:
                message.update(data)
                
            self.ser.write((json.dumps(message) + '\n').encode())
            return True
        except Exception as e:
            self.logger.error(f"Error al enviar comando: {e}")
            return False
    
    def disconnect(self):
        """Cierra la conexión serial"""
        if self.connected and self.ser:
            self.ser.close()
            self.connected = False
            self.logger.info("Conexión con Arduino cerrada")
    
    def __del__(self):
        self.disconnect()