"""
Paquete handlers - Módulos para procesamiento específico de mensajes

Este paquete contiene todos los handlers especializados para procesar diferentes tipos de mensajes.
Cada handler hereda de BaseMessageHandler e implementa su propia lógica de procesamiento.
"""

from .base_handler import BaseMessageHandler
from .acceso_vehiculo_handler import AccesoVehiculoHandler
from .parqueo_handler import ParqueoHandler

# Versión del paquete
__version__ = "1.0.0"

# Lista de handlers disponibles para importación directa
__all__ = [
    'BaseMessageHandler',
    'AccesoVehiculoHandler', 
    'ParqueoHandler'
]

# Inicialización común del paquete (opcional)
def init_package():
    """Inicialización común del paquete handlers"""
    import logging
    logging.info(f"Inicializando paquete handlers v{__version__}")

# Inicialización automática al importar el paquete
init_package()