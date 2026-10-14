import { useEffect, useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import api from '../api/client';

export default function TaskDetailsPage() {
  const { taskId } = useParams();
  const navigate = useNavigate();
  const [task, setTask] = useState(null);
  const [error, setError] = useState('');

  useEffect(() => {
    api.get(`/tasks/${taskId}`)
      .then(({ data }) => setTask(data))
      .catch((err) => setError(err.response?.data?.message || 'Task not found.'));
  }, [taskId]);

  const handleStatusChange = async (status) => {
    try {
      const { data } = await api.patch(`/tasks/${taskId}/status`, { status });
      setTask(data);
    } catch (err) {
      setError(err.response?.data?.message || 'Unable to update status.');
    }
  };

  if (!task) {
    return <div className="page-shell">{error || 'Loading task...'}</div>;
  }

  return (
    <div className="page-shell task-details">
      <div className="page-header detail-header">
        <div>
          <p className="eyebrow">Details</p>
          <h1>{task.title}</h1>
        </div>
        <button className="secondary-button" onClick={() => navigate(`/tasks/${taskId}/edit`)}>Edit</button>
      </div>

      {error && <div className="error-banner">{error}</div>}

      <div className="detail-panel">
        <p><strong>Description:</strong> {task.description || 'None'}</p>
        <p><strong>Status:</strong> {task.status}</p>
        <p><strong>Priority:</strong> {task.priority}</p>
        <p><strong>Due Date:</strong> {task.dueDate ? new Date(task.dueDate).toLocaleString() : 'Not set'}</p>
      </div>

      <div className="status-actions">
        <button className="secondary-button" onClick={() => handleStatusChange('TODO')}>Mark TODO</button>
        <button className="secondary-button" onClick={() => handleStatusChange('IN_PROGRESS')}>Mark In Progress</button>
        <button className="secondary-button" onClick={() => handleStatusChange('COMPLETED')}>Mark Completed</button>
      </div>
    </div>
  );
}
