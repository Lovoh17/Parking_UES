import logging
from abc import ABC, abstractmethod

class BaseMessageHandler(ABC):
    """Clase base para todos los handlers de mensajes"""
    
    def __init__(self, name):
        self.name = name
        self.logger = logging.getLogger(f"Handler.{name}")
        self.callbacks = []
    
    def add_callback(self, callback):
        self.callbacks.append(callback)
    
    @abstractmethod
    def handle(self, data, timestamp):
        pass
    
    def notify_callbacks(self, result):
        for callback in self.callbacks:
            try:
                callback(result)
            except Exception as e:
                self.logger.error(f"Error en callback: {e}")