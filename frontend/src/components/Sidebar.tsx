import React from 'react';
import { Link, useLocation } from 'react-router-dom';

const Sidebar: React.FC = () => {
  const location = useLocation();

  const isActive = (path: string) => {
    return location.pathname === path ? 'active' : '';
  };

  return (
    <aside className="sidebar">
      <nav>
        <ul className="sidebar-nav">
          <li>
            <Link to="/dashboard" className={isActive('/dashboard')}>
              📊 대시보드
            </Link>
          </li>
          <li>
            <Link to="/messages" className={isActive('/messages')}>
              📨 메시지 목록
            </Link>
          </li>
          <li>
            <Link to="/join-events" className={isActive('/join-events')}>
              🔗 JOIN 이벤트
            </Link>
          </li>
        </ul>
      </nav>
    </aside>
  );
};

export default Sidebar;