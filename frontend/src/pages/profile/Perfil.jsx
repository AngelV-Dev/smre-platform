import { useAuth } from '../../context/AuthContext';

export default function Perfil() {
  const { auth } = useAuth();
  const user = auth?.user;

  return (
    <div className="p-4">
      <div className="smre-title-container">
        <div>
          <h1>Mi Perfil</h1>
          <p>Visualiza y administra tus datos personales.</p>
        </div>
      </div>
      
      <div className="bg-white p-6 rounded-lg shadow mt-4 max-w-2xl">
        <div className="flex items-center space-x-6 mb-6">
          <div className="w-24 h-24 bg-blue-100 rounded-full flex items-center justify-center text-3xl text-blue-600 font-bold">
            {user?.nombre?.charAt(0) || 'U'}
          </div>
          <div>
            <h2 className="text-2xl font-bold">{user?.nombre || 'Usuario'}</h2>
            <p className="text-gray-500">{user?.role || 'Rol Desconocido'}</p>
          </div>
        </div>

        <div className="space-y-4">
          <div>
            <label className="block text-sm font-medium text-gray-700">Nombre</label>
            <input type="text" disabled value={user?.nombre || ''} className="smre-input mt-1 bg-gray-50" />
          </div>
          <div>
            <label className="block text-sm font-medium text-gray-700">Email</label>
            <input type="email" disabled value={user?.email || ''} className="smre-input mt-1 bg-gray-50" />
          </div>
          {/* Aquí se podrían añadir más campos según el modelo en el backend */}
        </div>
      </div>
    </div>
  );
}
