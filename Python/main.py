from core.parking_manager import ParkingSystem
import logging

def setup_logging():
    logging.basicConfig(
        level=logging.INFO,
        format='%(asctime)s - %(name)s - %(levelname)s - %(message)s',
        handlers=[
            logging.FileHandler('parking_system.log'),
            logging.StreamHandler()
        ]
    )

def main():
    setup_logging()
    
    try:
        system = ParkingSystem(config_file='config.json')
        system.start_system()
        
        # Mantener el programa en ejecución
        while True:
            pass
            
    except KeyboardInterrupt:
        print("\nDeteniendo el sistema...")
        system.stop_system()
    except Exception as e:
        logging.error(f"Error en el sistema: {e}")
        system.stop_system()

if __name__ == "__main__":
    main()