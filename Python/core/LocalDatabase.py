import sqlite3
from datetime import datetime
import logging
import json

class LocalDatabase:
    """Maneja la base de datos SQLite local"""
    
    def __init__(self, db_path='parking.db'):
        self.db_path = db_path
        self.conn = None
        self.logger = logging.getLogger(__name__)
        
    def connect(self):
        """Establece conexión con la base de datos"""
        try:
            self.conn = sqlite3.connect(self.db_path)
            self._create_tables()
            self.logger.info(f"Conectado a la base de datos local: {self.db_path}")
            return True
        except Exception as e:
            self.logger.error(f"Error al conectar a la base de datos: {e}")
            return False
    
    def _create_tables(self):
        """Crea las tablas necesarias"""
        cursor = self.conn.cursor()
        
        tables = {
            'vehicles': '''
            CREATE TABLE IF NOT EXISTS vehicles (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                plate_number TEXT NOT NULL,
                entry_time DATETIME NOT NULL,
                exit_time DATETIME,
                payment REAL DEFAULT 0,
                payment_status INTEGER DEFAULT 0,
                monthly_subscription INTEGER DEFAULT 0,
                allowed_start_time TEXT,
                allowed_end_time TEXT,
                violation INTEGER DEFAULT 0,
                synced INTEGER DEFAULT 0
            )''',
            
            'parking_spaces': '''
            CREATE TABLE IF NOT EXISTS parking_spaces (
                id INTEGER PRIMARY KEY,
                occupied INTEGER DEFAULT 0,
                vehicle_id INTEGER,
                FOREIGN KEY(vehicle_id) REFERENCES vehicles(id)
            )''',
            
            'environmental_data': '''
            CREATE TABLE IF NOT EXISTS environmental_data (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                temperature REAL,
                gas_level REAL,
                timestamp DATETIME DEFAULT CURRENT_TIMESTAMP,
                synced INTEGER DEFAULT 0
            )'''
        }
        
        for table_name, ddl in tables.items():
            try:
                cursor.execute(ddl)
                self.logger.debug(f"Tabla {table_name} creada/verificada")
            except Exception as e:
                self.logger.error(f"Error al crear tabla {table_name}: {e}")
        
        self.conn.commit()
    
    # ... (otros métodos para operaciones CRUD)
    
    def close(self):
        """Cierra la conexión con la base de datos"""
        if self.conn:
            self.conn.close()
            self.logger.info("Conexión con base de datos local cerrada")
    
    def __del__(self):
        self.close()