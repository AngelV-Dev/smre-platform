export default function Navbar() {
  return (
    <header
      className="fixed top-0 left-0 right-0 h-23 z-23 shadow-md"
      style={{ backgroundColor: '#3dc9d9' }}
    >
      <div
        className="absolute top-0 left-0 bg-white shadow-2xl rounded-br-lg flex items-center justify-center"
        style={{
          width: '310px',
          height: '130px'
        }}
      >
        <img
          src="/src/assets/logo.png"
          alt="Tecsup"
          className="h-60 object-contain"
        />
      </div>

      <div className="h-full flex justify-end items-center pr-8">
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
      </div>
    </header>
  )
}