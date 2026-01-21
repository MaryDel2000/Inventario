from authentik.core.models import User
try:
    u = User.objects.get(username='akadmin')
    u.is_superuser = True
    u.is_staff = True
    u.set_password('MariaStaff2024!')
    u.save()
    print("Successfully updated akadmin to superuser.")
except User.DoesNotExist:
    User.objects.create_superuser('akadmin', 'akadmin@example.com', 'MariaStaff2024!')
    print("Successfully created akadmin superuser.")
