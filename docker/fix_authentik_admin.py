#!/usr/bin/env python3
"""
Script para restaurar permisos de administrador en Authentik
"""

from authentik.core.models import User, Group
from authentik.rbac.models import Role

# Listar todos los usuarios
print("\n=== Usuarios existentes ===")
users = User.objects.all()
for user in users:
    print(f"- Username: {user.username}, Email: {user.email}, UUID: {user.uuid}")
    print(f"  Grupos: {[g.name for g in user.ak_groups.all()]}")
    print(f"  Activo: {user.is_active}")
    print()

# Obtener el grupo de administradores
try:
    admin_group = Group.objects.get(name="authentik Admins")
    print(f"✓ Grupo de administradores encontrado: {admin_group.name}")
except Group.DoesNotExist:
    print("✗ El grupo 'authentik Admins' no existe")
    # Intentar crear el grupo
    admin_group = Group.objects.create(name="authentik Admins", is_superuser=True)
    print(f"✓ Grupo 'authentik Admins' creado")

# Pedir al usuario que ingrese el username o email
print("\n=== Restaurar permisos ===")
print("Ingresa el username o email del usuario al que quieres restaurar permisos admin:")
user_identifier = input("> ").strip()

# Buscar el usuario
try:
    user = User.objects.get(username=user_identifier)
except User.DoesNotExist:
    try:
        user = User.objects.get(email=user_identifier)
    except User.DoesNotExist:
        print(f"✗ Usuario '{user_identifier}' no encontrado")
        exit(1)

print(f"\n✓ Usuario encontrado: {user.username} ({user.email})")

# Agregar al grupo de administradores
user.ak_groups.add(admin_group)
user.save()

print(f"✓ Usuario {user.username} agregado al grupo 'authentik Admins'")
print(f"✓ Permisos de administrador restaurados exitosamente!")

# Verificar
print(f"\nGrupos del usuario {user.username}:")
for group in user.ak_groups.all():
    print(f"  - {group.name}")
