import { useEffect, useState } from 'react';
import TaskCard from '../components/TaskCard';
import api from '../api/client';

export default function DashboardPage() {
  const [tasks, setTasks] = useState([]);
  const [error, setError] = useState('');

  const loadTasks = async () => {
    try {
      const { data } = await api.get('/tasks');
      setTasks(data.items || data);
    } catch (err) {
      setError(err.response?.data?.message || 'Unable to load tasks.');
    }
  };

  useEffect(() => {
    loadTasks();
  }, []);

  const handleDelete = async (taskId) => {
    try {
      await api.delete(`/tasks/${taskId}`);
      setTasks((current) => current.filter((task) => task.id !== taskId));
    } catch (err) {
      setError(err.response?.data?.message || 'Task could not be deleted.');
    }
  };

  return (
    <div className="page-shell">
      <div className="page-header">
        <div>
          <p className="eyebrow">Overview</p>
          <h1>Dashboard</h1>
        </div>
      </div>

      {error && <div className="error-banner">{error}</div>}

      <div className="task-grid">
        {tasks.length === 0 ? (
          <div className="empty-state">No tasks available yet.</div>
        ) : (
          tasks.map((task) => <TaskCard key={task.id} task={task} onDelete={handleDelete} />)
        )}
      </div>
    </div>
  );
}
