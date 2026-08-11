export default function Configuracion() {
  return (
    <div className="p-4">
      <div className="smre-title-container">
        <div>
          <h1>Configuración</h1>
          <p>Ajustes generales de tu cuenta y preferencias.</p>
        </div>
      </div>
      
      <div className="bg-white p-6 rounded-lg shadow mt-4 max-w-2xl">
        <h3 className="text-xl font-bold mb-4">Preferencias de la aplicación</h3>
        
        <div className="space-y-4">
          <div className="flex items-center justify-between">
            <span className="text-gray-700 font-medium">Recibir notificaciones por correo</span>
            <input type="checkbox" className="h-5 w-5 text-blue-600 rounded" defaultChecked />
          </div>
          <div className="flex items-center justify-between">
            <span className="text-gray-700 font-medium">Modo oscuro (Proximamente)</span>
            <input type="checkbox" className="h-5 w-5 text-blue-600 rounded" disabled />
          </div>
        </div>

        <div className="mt-8 border-t pt-4">
          <h3 className="text-xl font-bold mb-4 text-red-600">Zona de Peligro</h3>
          <button className="smre-btn-primary bg-red-600 hover:bg-red-700">
            Eliminar cuenta
          </button>
        </div>
      </div>
    </div>
  );
}
