import firebase_admin
from firebase_admin import credentials
import logging

def init_firebase():
    """Inicializa Firebase Admin SDK si no está ya inicializado"""
    logger = logging.getLogger(__name__)
    
    try:
        if not firebase_admin._apps:
            cred = credentials.Certificate("serviceAccountKey.json")
            firebase_admin.initialize_app(cred, {
                'databaseURL': "https://parkingues-69cfa-default-rtdb.firebaseio.com"
            })
            logger.info("Firebase inicializado correctamente")
            return True
        return True
    except Exception as e:
        logger.error(f"Error inicializando Firebase: {e}")
        return False