import firebase_admin
from firebase_admin import credentials
from firebase_admin import db
import time

# Configuración inicial de Firebase
def initialize_firebase():
    # Reemplaza con la ruta a tu archivo de credenciales de servicio
    cred = credentials.Certificate("Python/parkingues-firebase-adminsdk-fbsvc-3e054cdf4a.json")
    
    # Inicializa la app de Firebase con tu URL de base de datos
    firebase_admin.initialize_app(cred, {
        'databaseURL': 'https://parkingues-default-rtdb.firebaseio.com'
    })

# Manejador de eventos para cambios en la base de datos
def listener(event):
    print("\nEvento recibido:")
    print("Ruta:", event.path)
    print("Datos:", event.data)
    print("Tipo de evento:", event.event_type)

# Función principal
def main():
    initialize_firebase()
    
    # Referencia a la ubicación de la base de datos que quieres escuchar
    ref = db.reference('users')
    
    print("Iniciando escucha de cambios en Firebase...")
    
    # Registra el listener
    ref.listen(listener)
    
    # Mantén el script corriendo
    try:
        while True:
            time.sleep(1)
    except KeyboardInterrupt:
        print("\nDeteniendo la escucha...")

if __name__ == "__main__":
    main()