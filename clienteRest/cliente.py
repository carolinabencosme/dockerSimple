import os
import json
import urllib.request
import urllib.parse
import jwt

# Configuración
PORT = int(os.getenv("PORT_REST", 7000))
BASE_URL = f"http://localhost:{PORT}"
LOGIN_URL = f"{BASE_URL}/auth/login"
FULL_URLS_ENDPOINT = f"{BASE_URL}/urls"
USER_URLS_ENDPOINT = f"{BASE_URL}/users/{{user_id}}/urls"

# Colores
CYAN = "\033[96m"
MAGENTA = "\033[38;2;251;146;60m"
YELLOW = "\033[93m"
RED = "\033[91m"
WHITE = "\033[97m"
RESET = "\033[0m"
GREEN = "\033[32m"
BLUE = "\033[34m"

# Variables globales
token = None
user_id = None


def login():
    global token, user_id
    print("Inicio de sesión")
    username = input("Usuario: ")
    password = input("Contraseña: ")
    data = json.dumps({"username": username, "password": password}).encode()
    try:
        req = urllib.request.Request(LOGIN_URL, data=data, method="POST")
        req.add_header("Content-Type", "application/json")
        response = urllib.request.urlopen(req)
        if response.status == 200:
            res_data = json.loads(response.read().decode())
            token = res_data["token"]
            decoded = jwt.decode(token, options={"verify_signature": False})
            user_id = decoded.get("user_id")
            print(f"{GREEN} Login exitoso. ID de usuario: {user_id}{RESET}")
        else:
            print(f"{RED} Error al autenticar.{RESET}")
    except Exception as e:
        print(f"{RED}Error durante login: {e}{RESET}")


def crear_url():
    original = input(" Ingresa la URL a acortar: ")
    payload = json.dumps(
        {"originalUrl": original, "createdBy": {"id": user_id}}
    ).encode()

    try:
        req = urllib.request.Request(
            FULL_URLS_ENDPOINT, data=payload, method="POST")
        req.add_header("Content-Type", "application/json")
        req.add_header("Authorization", f"Bearer {token}")
        response = urllib.request.urlopen(req)
        if response.status == 201:
            res_data = json.loads(response.read().decode())
            print(f"{GREEN} URL creada:")
            print(f" - Original: {res_data['originalUrl']}")
            print(f" - Acortada: {res_data['shortenedUrl']}")
            print(f" - Fecha: {res_data['createdAt']}")
            print(f" - Clicks: {res_data['stats']['clickCount']}")
            print(
                f" - Vista previa: {res_data['sitePreviewBase64'][:30]}...{RESET}")
        else:
            print(f"{RED} Error al crear URL.{RESET}")
    except Exception as e:
        print(f"{RED}Error al crear URL: {e}{RESET}")


def listar_urls():
    url = USER_URLS_ENDPOINT.format(user_id=user_id)
    try:
        req = urllib.request.Request(url, method="GET")
        req.add_header("Authorization", f"Bearer {token}")
        response = urllib.request.urlopen(req)
        if response.status == 200:
            data = json.loads(response.read().decode())

            print(f"\n{BLUE}┌───────────────────────────────────────┐{RESET}")
            print(f"{BLUE}│       📋 Lista de URLs registradas     │{RESET}")
            print(f"{BLUE}└───────────────────────────────────────┘{RESET}\n")

            items = [data] if isinstance(data, dict) else data

            if not items:
                print(f"{YELLOW}⚠ No hay datos para mostrar.{RESET}")
                return

            for i, item in enumerate(items, 1):
                print(f"{CYAN}╔═════════════ URL #{
                      i:02d} ═════════════╗{RESET}")
                for key, value in item.items():
                    if key == "analytics" and isinstance(value, list):
                        print(f"{MAGENTA}║ 📊 Analytics:{RESET}")
                        if not value:
                            print(
                                f"{MAGENTA}║   └ 🟠 Sin datos de análisis{RESET}")
                        else:
                            for j, analytic in enumerate(value, 1):
                                print(f"{MAGENTA}║   ┌ 📈 Entrada #{j}{RESET}")
                                for sub_key, sub_value in analytic.items():
                                    sub_k = sub_key.replace("_", " ").title()
                                    sub_v = str(sub_value)[:40] + (
                                        "..." if len(
                                            str(sub_value)) > 40 else ""
                                    )
                                    print(
                                        f"{MAGENTA}║   │{RESET} {WHITE}{sub_k:<15}:{
                                            RESET
                                        } {sub_v}"
                                    )
                                print(
                                    f"{MAGENTA}║   └────────────────────────────{
                                        RESET}"
                                )
                    else:
                        label = key.replace("_", " ").title()
                        value_str = str(value)[:50] + (
                            "..." if len(str(value)) > 50 else ""
                        )
                        print(f"{CYAN}║{RESET} {WHITE}{
                              label:<18}:{RESET} {value_str}")
                print(f"{CYAN}╚═══════════════════════════════════════╝{RESET}\n")
    except Exception as e:
        print(f"{RED}Error al crear URL: {e}{RESET}")


def main():
    print(f"{BLUE}=== Cliente REST: Acortador de URLs ==={RESET}")
    login()
    if not token or not user_id:
        print(f"{RED} No se pudo autenticar. Saliendo...{RESET}")
        return

    while True:
        print("\nMenú:")
        print("1. Crear nueva URL")
        print("2. Listar mis URLs")
        print("3. Salir")

        opcion = input("Selecciona una opción: ")

        if opcion == "1":
            crear_url()
        elif opcion == "2":
            listar_urls()
        elif opcion == "3":
            print(f"{YELLOW}Saliendo del cliente...{RESET}")
            break
        else:
            print(f"{RED} Opción inválida{RESET}")


if __name__ == "__main__":
    main()
