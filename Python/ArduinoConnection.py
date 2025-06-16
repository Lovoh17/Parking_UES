import serial
import serial.tools.list_ports
from time import sleep

class ArduinoConnector:
    """
    Clase mejorada para manejar la conexión con Arduino (compatible con Linux/Windows/macOS).
    
    Args:
        baudrate (int): Velocidad en baudios (por defecto 9600)
        timeout (float): Tiempo de espera para la comunicación serial (por defecto 1 segundo)
    """
    
    def __init__(self, baudrate=9600, timeout=1):
        self.baudrate = baudrate
        self.timeout = timeout
        self.port = None
        self.ser = None
        self.connected = False
        
    def find_arduino_port(self):
        """
        Detecta automáticamente el puerto del Arduino en diferentes sistemas operativos.
        
        Returns:
            str or None: Nombre del puerto si se encuentra, None si no se detecta
        """
        ports = serial.tools.list_ports.comports()
        
        # Patrones de búsqueda mejorados
        search_patterns = [
            'Arduino',    # Descripción estándar
            'CH340',      # Para clones con chip CH340
            'ACM',        # Linux (ttyACM*)
            'USB',        # Linux (ttyUSB*)
            'USB Serial', # Algunas placas
            'Mega',       # Arduino Mega
            'UART'       # Dispositivos seriales
        ]
        
        for port in ports:
            # Busca coincidencias en descripción y nombre del dispositivo
            port_info = f"{port.description} {port.device}".lower()
            if any(pattern.lower() in port_info for pattern in search_patterns):
                print(f"Dispositivo detectado: {port.device} - {port.description}")
                return port.device
                
        print("No se encontró ningún Arduino conectado. Puertos disponibles:")
        for port in ports:
            print(f"- {port.device}: {port.description}")
        return None
    
    def connect(self, port_name=None):
        """
        Establece la conexión con el Arduino.
        
        Args:
            port_name (str, optional): Nombre del puerto. Si es None, intenta autodetectar.
            
        Returns:
            bool: True si la conexión fue exitosa, False en caso contrario
        """
        try:
            if port_name is None:
                port_name = self.find_arduino_port()
                if port_name is None:
                    print("Error: No se pudo detectar el puerto del Arduino.")
                    return False
            
            self.port = port_name
            self.ser = serial.Serial(
                port=self.port,
                baudrate=self.baudrate,
                timeout=self.timeout
            )
            
            # Espera para inicialización (2 segundos para Arduino Mega)
            sleep(2)
            
            # Limpia el buffer de entrada por si hay datos residuales
            self.ser.reset_input_buffer()
            
            self.connected = True
            print(f"Conexión exitosa con Arduino en {self.port}")
            return True
            
        except serial.SerialException as e:
            print(f"Error de conexión serial: {str(e)}")
        except Exception as e:
            print(f"Error inesperado: {str(e)}")
            
        self.connected = False
        return False
    
    def disconnect(self):
        """Cierra la conexión serial si está abierta."""
        if self.connected and self.ser is not None:
            try:
                self.ser.close()
                print(f"Conexión cerrada: {self.port}")
            except Exception as e:
                print(f"Error al cerrar conexión: {str(e)}")
            finally:
                self.connected = False
    
    def send_data(self, data):
        """
        Envía datos al Arduino.
        
        Args:
            data (str): Datos a enviar (deben terminar con \n)
            
        Returns:
            bool: True si los datos se enviaron correctamente
        """
        if not self.connected:
            print("Error: No hay conexión con el Arduino")
            return False
            
        try:
            if not data.endswith('\n'):
                data += '\n'
            self.ser.write(data.encode('utf-8'))
            return True
        except Exception as e:
            print(f"Error al enviar datos: {str(e)}")
            return False
    
    def read_data(self):
        """
        Lee datos disponibles desde el Arduino.
        
        Returns:
            str or None: Datos recibidos (sin saltos de línea) o None si no hay datos
        """
        if not self.connected:
            print("Error: No hay conexión con el Arduino")
            return None
            
        try:
            if self.ser.in_waiting > 0:
                line = self.ser.readline().decode('utf-8').strip()
                return line if line else None
            return None
        except Exception as e:
            print(f"Error al leer datos: {str(e)}")
            return None
    
    def monitor_door_status(self):
        """
        Monitorea constantemente el estado de la puerta desde el Arduino.
        
        Returns:
            str or None: Mensaje de estado de la puerta o None si hay error
        """
        if not self.connected:
            print("Error: No hay conexión con el Arduino")
            return None
            
        try:
            # Limpiar buffer antes de empezar
            self.ser.reset_input_buffer()
            
            while True:
                if self.ser.in_waiting > 0:
                    line = self.ser.readline().decode('utf-8').strip()
                    if line:
                        print(f"Estado actual: {line}")
                        return line
                sleep(0.1)
                
        except Exception as e:
            print(f"Error en monitoreo: {str(e)}")
            return None
    
    def __del__(self):
        """Destructor que cierra la conexión al eliminar el objeto."""
        self.disconnect()


# Ejemplo de uso con el sistema ultrasónico
if __name__ == "__main__":
    print("=== Sistema de Monitoreo de Puerta Automática ===")
    
    arduino = ArduinoConnector(baudrate=9600)
    
    if arduino.connect():
        try:
            print("\nSistema iniciado. Monitoreando estado de la puerta...")
            print("Presiona Ctrl+C para salir\n")
            
            while True:
                # Monitorear constantemente el estado de la puerta
                status = arduino.monitor_door_status()
                if status:
                    # Aquí puedes agregar lógica adicional basada en el estado
                    pass
                
        except KeyboardInterrupt:
            print("\nPrograma terminado por el usuario")
        finally:
            arduino.disconnect()
    else:
        print("No se pudo establecer conexión con el Arduino")