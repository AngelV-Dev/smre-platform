import SideBar from './SideBar';
import Navbar from './Navbar';
import { Outlet } from 'react-router-dom';
import './Layout.css';

const Layout = () => {
  return (
    <div className="layout-container">
      <SideBar />
      <div className="main-content">
        <Navbar />
        <div className="page-content">
          <Outlet /> {/* Aquí se renderizarán las páginas hijas */}
        </div>
      </div>
    </div>
  );
};

export default Layout;