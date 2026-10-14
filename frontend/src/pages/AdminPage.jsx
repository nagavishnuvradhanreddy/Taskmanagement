import { useEffect, useState } from 'react';
import api from '../api/client';

export default function AdminPage() {
  const [metrics, setMetrics] = useState(null);
  const [error, setError] = useState('');

  useEffect(() => {
    api.get('/admin/metrics')
      .then(({ data }) => setMetrics(data))
      .catch((err) => setError(err.response?.data?.message || 'Unable to load admin metrics.'));
  }, []);

  if (!metrics) {
    return <div className="page-shell">{error || 'Loading admin metrics...'}</div>;
  }

  return (
    <div className="page-shell">
      <div className="page-header">
        <div>
          <p className="eyebrow">Administration</p>
          <h1>Insights</h1>
        </div>
      </div>

      <div className="metrics-grid">
        <div className="metric-card">
          <p>Total tasks</p>
          <h2>{metrics.totalTasks}</h2>
        </div>
        <div className="metric-card">
          <p>Completed</p>
          <h2>{metrics.completedTasks}</h2>
        </div>
        <div className="metric-card">
          <p>Open</p>
          <h2>{metrics.openTasks}</h2>
        </div>
      </div>
    </div>
  );
}
