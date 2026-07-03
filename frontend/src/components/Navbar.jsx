import { useNavigate } from 'react-router-dom';

export default function Navbar() {
  const navigate = useNavigate();

  const handleLogout = () => {
    sessionStorage.removeItem('smre_token');
    sessionStorage.removeItem('smre_user');
    navigate('/login', { replace: true });
  };

  return (
    <header
      className="fixed top-0 left-0 right-0 h-23 z-23 shadow-md"
      style={{ backgroundColor: '#3dc9d9' }}
    >
      <div
        className="absolute top-0 left-0 bg-white shadow-2xl rounded-br-lg flex items-center justify-center cursor-pointer"
        style={{
          width: '310px',
          height: '130px'
        }}
        onClick={() => navigate('/admin/dashboard')}
      >
        <img
          src="/src/assets/logo.png"
          alt="Tecsup"
          className="h-60 object-contain"
        />
      </div>

      <div className="h-full flex justify-end items-center pr-8">
        <button 
          onClick={handleLogout}
          aria-label="Cerrar Sesión"
          className="bg-transparent border-none cursor-pointer text-white p-2 hover:opacity-85 transition"
        >
          <svg
            className="w-8 h-8 text-white"
            fill="none"
            stroke="currentColor"
            viewBox="0 0 24 24"
          >
            <path
              strokeWidth="2"
              strokeLinecap="round"
              strokeLinejoin="round"
              d="M12 15.75A3.75 3.75 0 1112 8.25a3.75 3.75 0 010 7.5z"
            />
          </svg>
        </button>
      </div>
    </header>
  )
}