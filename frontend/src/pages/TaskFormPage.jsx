import { useEffect, useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import TaskForm from '../components/TaskForm';
import api from '../api/client';

export default function TaskFormPage() {
  const navigate = useNavigate();
  const { taskId } = useParams();
  const isEditing = Boolean(taskId);
  const [initialValues, setInitialValues] = useState(null);
  const [error, setError] = useState('');

  useEffect(() => {
    if (!isEditing) return;

    api.get(`/tasks/${taskId}`)
      .then(({ data }) => setInitialValues(data))
      .catch((err) => setError(err.response?.data?.message || 'Unable to load task.'));
  }, [isEditing, taskId]);

  const handleSubmit = async (payload) => {
    try {
      if (isEditing) {
        await api.put(`/tasks/${taskId}`, payload);
      } else {
        await api.post('/tasks', payload);
      }
      navigate('/dashboard');
    } catch (err) {
      setError(err.response?.data?.message || 'Unable to save task.');
    }
  };

  if (isEditing && !initialValues) {
    return <div className="page-shell">Loading task...</div>;
  }

  return (
    <div className="page-shell">
      <div className="page-header">
        <div>
          <p className="eyebrow">Task</p>
          <h1>{isEditing ? 'Edit task' : 'Create task'}</h1>
        </div>
      </div>

      {error && <div className="error-banner">{error}</div>}

      <TaskForm initialValues={initialValues || {}} onSubmit={handleSubmit} submitLabel={isEditing ? 'Update Task' : 'Create Task'} />
    </div>
  );
}
