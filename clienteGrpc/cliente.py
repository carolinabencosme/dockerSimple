import os
import json
import jwt
import urllib.request
import grpc
import urlshortener_pb2
import urlshortener_pb2_grpc

# Configuración
PORT_GRPC = int(os.getenv("PORT_GRPC", 9090))
SERVER_ADDRESS = f"localhost:{PORT_GRPC}"
PORT_REST = int(os.getenv("PORT_REST", 7000))
REST_LOGIN_URL = f"http://localhost:{PORT_REST}/auth/login"

# Colores
CYAN    = "\033[96m"
MAGENTA = "\033[38;2;251;146;60m"
YELLOW  = "\033[93m"
RED     = "\033[91m"
WHITE   = "\033[97m"
RESET   = "\033[0m"
GREEN   = "\033[32m"
BLUE    = "\033[34m"

# Variables globales
user_id = None
token = None

def login_rest():
    global user_id, token
    print("Inicio de sesión")
    username = input("Usuario: ")
    password = input("Contraseña: ")
    payload = json.dumps({"username": username, "password": password}).encode()

    try:
        req = urllib.request.Request(REST_LOGIN_URL, data=payload, method="POST")
        req.add_header("Content-Type", "application/json")
        response = urllib.request.urlopen(req)
        if response.status == 200:
            data = json.loads(response.read().decode())
            token = data["token"]
            decoded = jwt.decode(token, options={"verify_signature": False})
            user_id = decoded.get("user_id")
            print(f"{GREEN} Login exitoso. ID de usuario: {user_id}{RESET}")
        else:
            print(f"{RED} Error al autenticar.{RESET}")
    except Exception as e:
        print(f"{RED} Error durante login: {e}{RESET}")

def crear_url():
    channel = grpc.insecure_channel(SERVER_ADDRESS)
    stub   = urlshortener_pb2_grpc.UrlShortenerServiceStub(channel)

    original = input(" Ingresa la URL a acortar: ")
    request  = urlshortener_pb2.CreateUrlRequest(
        user_id      = user_id,
        original_url = original
    )

    try:
        response = stub.CreateUrl(request)
        ud       = response.url   # <— aquí el UrlData

        print(f"{GREEN} URL creada:")
        print(f"{CYAN}╔═══════════════════════════════════════╗{RESET}")
        print(f"{CYAN}║{RESET} {WHITE}Original URL       :{RESET} {ud.original_url}")
        print(f"{CYAN}║{RESET} {WHITE}Acortada           :{RESET} {ud.shortened_url}")
        print(f"{CYAN}║{RESET} {WHITE}Creada por         :{RESET} {ud.created_by}")
        print(f"{CYAN}║{RESET} {WHITE}Fecha              :{RESET} {ud.created_at}")
        # si quieres mostrar analytics (vacío tras creación):
        print(f"{MAGENTA}║ 📊 Analytics:{RESET}")
        if not ud.analytics:
            print(f"{MAGENTA}║   └ 🟠 Sin datos de análisis{RESET}")
        print(f"{CYAN}╚═══════════════════════════════════════╝{RESET}")

    except grpc.RpcError as e:
        print(f"{RED} Error en CreateUrl: {e}{RESET}")
    finally:
        channel.close()


def listar_urls():
    channel = grpc.insecure_channel(SERVER_ADDRESS)
    stub = urlshortener_pb2_grpc.UrlShortenerServiceStub(channel)

    request = urlshortener_pb2.ListUserUrlsRequest(user_id=user_id)
    try:
        response = stub.ListUserUrls(request)
        print(f"\n{BLUE}┌───────────────────────────────────────┐{RESET}")
        print(f"{BLUE}│       📋 Lista de URLs registradas     │{RESET}")
        print(f"{BLUE}└───────────────────────────────────────┘{RESET}\n")

        urls = response.urls
        if not urls:
            print(f"{YELLOW}⚠ No hay datos para mostrar.{RESET}")
            return

        for i, url_data in enumerate(urls, 1):
            print(f"{CYAN}╔═════════════ URL #{i:02d} ═════════════╗{RESET}")
            # Campos principales
            print(f"{CYAN}║{RESET} {WHITE}Original URL       :{RESET} {url_data.original_url}")
            print(f"{CYAN}║{RESET} {WHITE}Acortada           :{RESET} {url_data.shortened_url}")
            print(f"{CYAN}║{RESET} {WHITE}Fecha              :{RESET} {url_data.created_at}")

            # Analytics
            print(f"{MAGENTA}║ 📊 Analytics:{RESET}")
            analytics = list(url_data.analytics)
            if not analytics:
                print(f"{MAGENTA}║   └ 🟠 Sin datos de análisis{RESET}")
            else:
                for j, a in enumerate(analytics, 1):
                    print(f"{MAGENTA}║   ┌ 📈 Entrada #{j}{RESET}")
                    # Cada campo del analytic
                    print(f"{MAGENTA}║   │{RESET} {WHITE}{'Access Date':<15}:{RESET} {a.access_date}")
                    print(f"{MAGENTA}║   │{RESET} {WHITE}{'Browser':<15}:{RESET} {a.browser}")
                    print(f"{MAGENTA}║   │{RESET} {WHITE}{'Ip Adress':<15}:{RESET} {a.ip_adress}")
                    print(f"{MAGENTA}║   │{RESET} {WHITE}{'Os':<15}:{RESET} {a.os}")
                    print(f"{MAGENTA}║   └────────────────────────────{RESET}")
            print(f"{CYAN}╚═══════════════════════════════════════╝{RESET}\n")
    except grpc.RpcError as e:
        print(f"{RED} Error en ListUserUrls: {e}{RESET}")
    finally:
        channel.close()

def main():
    print(f"{BLUE}=== Cliente gRPC: Acortador de URLs ==={RESET}")
    login_rest()
    if not user_id:
        print(f"{RED} No se pudo obtener ID del usuario. Terminando...{RESET}")
        return

    while True:
        print(f"\n{YELLOW}=== Menú ==={RESET}")
        print("1. Crear nueva URL")
        print("2. Listar mis URLs")
        print("3. Salir")
        opcion = input("Selecciona una opción: ")
        if opcion == "1":
            crear_url()
        elif opcion == "2":
            listar_urls()
        elif opcion == "3":
            print(f"{BLUE}Cerrando cliente gRPC...{RESET}")
            break
        else:
            print(f"{RED} Opción inválida{RESET}")

if __name__ == "__main__":
    main()
