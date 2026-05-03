import { useState, useEffect } from 'react';
import ErrorBoundary from './ErrorBoundary';
import './index.css';

interface Control {
  id: number;
  title: string;
  description: string;
  status: string;
  riskLevel: string;
  deadline: string;
}

const API_URL = 'http://localhost:8080/api';

function App() {
  const [token, setToken] = useState<string | null>(localStorage.getItem('token'));
  const [activeTab, setActiveTab] = useState('Dashboard');
  const [controls, setControls] = useState<Control[]>([]);
  const [loading, setLoading] = useState(false);
  const [editingControl, setEditingControl] = useState<Partial<Control> | null>(null);

  // Login Form State
  const [username, setUsername] = useState('admin');
  const [password, setPassword] = useState('admin123');
  const [loginError, setLoginError] = useState('');

  const fetchControls = async (authToken: string) => {
    setLoading(true);
    try {
      const res = await fetch(`${API_URL}/controls/all?size=100`, {
        headers: { 'Authorization': `Bearer ${authToken}` }
      });
      if (res.ok) {
        const data = await res.json();
        setControls(data.content || []);
      } else if (res.status === 401 || res.status === 403) {
        handleLogout();
      }
    } catch (err) {
      console.error("Error fetching controls:", err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    if (token) fetchControls(token);
  }, [token]);

  const handleLogin = async (e: React.FormEvent) => {
    e.preventDefault();
    setLoginError('');
    try {
      const res = await fetch(`${API_URL}/auth/login`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ username, password })
      });
      if (res.ok) {
        const data = await res.json();
        setToken(data.token);
        localStorage.setItem('token', data.token);
      } else {
        setLoginError('Invalid credentials');
      }
    } catch (err) {
      setLoginError('Server connection failed');
    }
  };

  const handleLogout = () => {
    localStorage.removeItem('token');
    setToken(null);
    setControls([]);
    setActiveTab('Dashboard');
    window.location.reload(); // Force a clean state
  };

  const handleSave = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!editingControl) return;

    const isNew = !editingControl.id;
    const url = isNew ? `${API_URL}/controls/create` : `${API_URL}/controls/${editingControl.id}`;
    const method = isNew ? 'POST' : 'PUT';

    let safeDeadline = editingControl.deadline || new Date().toISOString();
    if (safeDeadline.endsWith('Z')) safeDeadline = safeDeadline.slice(0, -1);

    const payload = {
      title: editingControl.title || 'New Control',
      description: editingControl.description || '',
      status: editingControl.status || 'PENDING',
      riskLevel: editingControl.riskLevel || 'LOW',
      deadline: safeDeadline,
      assigneeId: 1
    };

    try {
      const res = await fetch(url, {
        method,
        headers: { 
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${token}`
        },
        body: JSON.stringify(payload)
      });

      if (res.ok) {
        await fetchControls(token!);
        setEditingControl(null);
        alert("Saved successfully!");
      } else {
        const errData = await res.json();
        alert(`Server Error: ${errData.message || 'Unknown error'}`);
      }
    } catch (err) {
      alert(`Connection Error: Check if backend is reachable.`);
    }
  };

  const handleDelete = async (id: number) => {
    if (!confirm('Are you sure you want to delete this control?')) return;
    try {
      const res = await fetch(`${API_URL}/controls/${id}`, {
        method: 'DELETE',
        headers: { 'Authorization': `Bearer ${token}` }
      });
      if (res.ok) {
        await fetchControls(token!);
      }
    } catch (err) {
      console.error(err);
    }
  };

  const isTokenValid = token && token !== 'null' && token !== 'undefined';

  if (!isTokenValid) {
    return (
      <div style={{ display: 'flex', justifyContent: 'center', alignItems: 'center', height: '100vh', background: 'linear-gradient(135deg, #0f172a 0%, #1e293b 100%)' }}>
        <div style={{ padding: '40px', backgroundColor: 'white', borderRadius: '16px', boxShadow: '0 25px 50px -12px rgba(0,0,0,0.5)', width: '360px' }}>
          <div style={{ textAlign: 'center', marginBottom: '32px' }}>
            <h2 style={{ fontFamily: 'Outfit', fontSize: '28px', color: '#0f172a', margin: '0 0 8px 0' }}>Welcome Back</h2>
            <p style={{ color: '#64748b', fontSize: '14px' }}>Log in to AuditShield</p>
          </div>
          <form onSubmit={handleLogin}>
            {loginError && <div style={{ background: '#fef2f2', color: '#991b1b', padding: '12px', borderRadius: '8px', fontSize: '13px', marginBottom: '20px' }}>{loginError}</div>}
            <div className="input-group">
              <label>Username</label>
              <input value={username} onChange={e => setUsername(e.target.value)} placeholder="e.g. admin" />
            </div>
            <div className="input-group" style={{ marginBottom: '24px' }}>
              <label>Password</label>
              <input type="password" value={password} onChange={e => setPassword(e.target.value)} placeholder="••••••••" />
            </div>
            <button type="submit" className="btn btn-primary" style={{ width: '100%', padding: '12px' }}>Sign In</button>
          </form>
        </div>
      </div>
    );
  }

  const renderContent = () => {
    switch(activeTab) {
      case 'Library':
        return (
          <div>
            <div className="header">
              <h1>Controls Library</h1>
              <div style={{ display: 'flex', gap: '12px' }}>
                <button onClick={() => fetchControls(token!)} className="btn btn-ghost">Refresh Data</button>
                <button onClick={() => setEditingControl({ title: '', status: 'PENDING', riskLevel: 'LOW' })} className="btn btn-primary">+ Add New</button>
              </div>
            </div>
            <div className="controls-list">
              {controls.map(control => (
                <div key={control.id} className="control-card">
                  <div className="control-info">
                    <h3>{control.title}</h3>
                    <p style={{ color: 'var(--secondary)', fontSize: '14px' }}>{control.description}</p>
                  </div>
                  <div style={{ display: 'flex', gap: '8px' }}>
                    <button onClick={() => setEditingControl(control)} className="btn btn-ghost">Edit</button>
                  </div>
                </div>
              ))}
            </div>
          </div>
        );
      case 'Risk':
        return (
          <div>
            <div className="header"><h1>Risk Assessment</h1></div>
            <div className="stats-grid">
              <div className="stat-card" style={{ borderLeft: '4px solid var(--danger)' }}>
                <div className="stat-label">High Risk Controls</div>
                <div className="stat-value">{controls.filter(c => c.riskLevel === 'HIGH').length}</div>
              </div>
            </div>
            <div className="controls-list">
              {controls.filter(c => c.riskLevel === 'HIGH').map(control => (
                <div key={control.id} className="control-card">
                  <div className="control-info">
                    <h3>{control.title}</h3>
                    <span className="badge badge-risk-high">High Risk</span>
                  </div>
                </div>
              ))}
            </div>
          </div>
        );
      case 'Reports':
        return (
          <div>
            <div className="header"><h1>Compliance Reports</h1></div>
            <div className="stat-card">
              <h3>Overall Progress</h3>
              <div style={{ background: '#e2e8f0', height: 20, borderRadius: 10, overflow: 'hidden', marginTop: 20 }}>
                <div style={{ background: 'var(--success)', height: '100%', width: `${(controls.filter(c => c.status === 'PASSED').length / (controls.length || 1)) * 100}%` }}></div>
              </div>
              <p style={{ marginTop: 10, color: 'var(--secondary)' }}>{Math.round((controls.filter(c => c.status === 'PASSED').length / (controls.length || 1)) * 100)}% of controls passed.</p>
            </div>
          </div>
        );
      default:
        return (
          <div>
            <header className="header">
              <h1>Dashboard Overview</h1>
              <button onClick={() => setEditingControl({ title: '', status: 'PENDING', riskLevel: 'LOW' })} className="btn btn-primary">+ New Control</button>
            </header>
            <section className="stats-grid">
              <div className="stat-card"><div className="stat-label">Total</div><div className="stat-value">{controls.length}</div></div>
              <div className="stat-card"><div className="stat-label">Passed</div><div className="stat-value" style={{ color: 'var(--success)' }}>{controls.filter(c => c.status === 'PASSED').length}</div></div>
              <div className="stat-card"><div className="stat-label">Failed</div><div className="stat-value" style={{ color: 'var(--danger)' }}>{controls.filter(c => c.status === 'FAILED').length}</div></div>
            </section>
            <div className="controls-list">
              {controls.slice(0, 5).map(control => (
                <div key={control.id} className="control-card">
                  <div className="control-info">
                    <h3>{control.title}</h3>
                    <div className="control-meta">
                      <span className={`badge badge-status-${control.status.toLowerCase()}`}>{control.status}</span>
                      <span className={`badge badge-risk-${control.riskLevel.toLowerCase()}`}>● {control.riskLevel}</span>
                    </div>
                  </div>
                  <div style={{ display: 'flex', gap: '8px' }}>
                    <button onClick={() => setEditingControl(control)} className="btn btn-ghost">Edit</button>
                    <button onClick={() => handleDelete(control.id)} className="btn btn-danger">Delete</button>
                  </div>
                </div>
              ))}
            </div>
          </div>
        );
    }
  };

  return (
    <ErrorBoundary>
      <div className="app-layout">
        <aside className="sidebar">
          <div className="logo"><div style={{ width: 24, height: 24, background: 'linear-gradient(45deg, #38bdf8, #2563eb)', borderRadius: 6 }}></div>AuditShield</div>
          <nav>
            <div onClick={() => setActiveTab('Dashboard')} className={`nav-item ${activeTab === 'Dashboard' ? 'active' : ''}`}>Dashboard</div>
            <div onClick={() => setActiveTab('Library')} className={`nav-item ${activeTab === 'Library' ? 'active' : ''}`}>Controls Library</div>
            <div onClick={() => setActiveTab('Risk')} className={`nav-item ${activeTab === 'Risk' ? 'active' : ''}`}>Risk Assessment</div>
            <div onClick={() => setActiveTab('Reports')} className={`nav-item ${activeTab === 'Reports' ? 'active' : ''}`}>Reports</div>
            <div className="nav-item">Settings</div>
          </nav>
          <button onClick={handleLogout} className="nav-item" style={{ background: 'transparent', border: 'none', width: '100%', textAlign: 'left', marginTop: 'auto' }}>Logout</button>
        </aside>

        <main className="main-content">
          {editingControl && (
            <div className="form-overlay">
              <div className="form-card">
                <h2 style={{ marginTop: 0, marginBottom: '24px' }}>{editingControl.id ? 'Edit Control' : 'Create Control'}</h2>
                <form onSubmit={handleSave}>
                  <div className="input-group"><label>Title</label><input value={editingControl.title || ''} onChange={e => setEditingControl({...editingControl, title: e.target.value})} required /></div>
                  <div className="input-group"><label>Description</label><textarea value={editingControl.description || ''} onChange={e => setEditingControl({...editingControl, description: e.target.value})} /></div>
                  <div style={{ display: 'flex', gap: '20px' }}>
                    <div className="input-group" style={{ flex: 1 }}><label>Status</label><select value={editingControl.status || 'PENDING'} onChange={e => setEditingControl({...editingControl, status: e.target.value})}><option value="PENDING">Pending</option><option value="PASSED">Passed</option><option value="FAILED">Failed</option></select></div>
                    <div className="input-group" style={{ flex: 1 }}><label>Risk Level</label><select value={editingControl.riskLevel || 'LOW'} onChange={e => setEditingControl({...editingControl, riskLevel: e.target.value})}><option value="LOW">Low</option><option value="MEDIUM">Medium</option><option value="HIGH">High</option></select></div>
                  </div>
                  <div style={{ display: 'flex', gap: '12px' }}><button type="submit" className="btn btn-primary" style={{ flex: 1 }}>Save</button><button type="button" onClick={() => setEditingControl(null)} className="btn btn-ghost" style={{ flex: 1 }}>Cancel</button></div>
                </form>
              </div>
            </div>
          )}
          {renderContent()}
        </main>
      </div>
    </ErrorBoundary>
  );
}

export default App;
