import { Navigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'

export default function PrivateRoute({ children, allowedRoles }) {
  const { auth } = useAuth()

  // 1. Si no hay sesión, al login
  if (!auth || !auth.user) {
    console.log("🔒 PrivateRoute: No hay objeto auth o auth.user activo");
    return <Navigate to="/login" replace />
  }

  // 2. Imprimimos en la consola exactamente qué guardó tu login para ver el misterio
  console.log("👥 Contenido de auth.user en este intento:", auth.user);

  // 3. Validación ultra-segura: extrae 'role' o 'rol', ignorando mayúsculas/minúsculas
  const rawRole = auth.user.role || auth.user.rol || auth.user.roleResp || "";
  const currentRole = rawRole.toString().toUpperCase().trim();

  console.log("🔑 Rol detectado y procesado por el filtro:", currentRole);
  console.log("📋 Roles permitidos para esta pantalla:", allowedRoles);

  // 4. Verificar si el rol está en la lista permitida
  if (allowedRoles && !allowedRoles.includes(currentRole)) {
    console.warn("🚫 Acceso denegado: El rol no coincide con los permitidos.");
    return <Navigate to="/acceso-denegado" replace />
  }

  return children
}
