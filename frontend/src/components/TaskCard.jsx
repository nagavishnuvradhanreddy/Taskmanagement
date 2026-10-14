import { Link } from 'react-router-dom';

const statusStyles = {
  TODO: 'status-todo',
  IN_PROGRESS: 'status-progress',
  COMPLETED: 'status-completed',
};

const priorityStyles = {
  LOW: 'priority-low',
  MEDIUM: 'priority-medium',
  HIGH: 'priority-high',
};

export default function TaskCard({ task, onDelete }) {
  return (
    <article className="task-card">
      <div className="task-card-header">
        <div>
          <span className={`status-badge ${statusStyles[task.status]}`}>{task.status}</span>
          <span className={`priority-badge ${priorityStyles[task.priority]}`}>{task.priority}</span>
        </div>
        <span className="task-date">{task.dueDate ? new Date(task.dueDate).toLocaleDateString() : 'No due date'}</span>
      </div>

      <h3>{task.title}</h3>
      <p>{task.description || 'No description provided.'}</p>

      <div className="task-card-actions">
        <Link to={`/tasks/${task.id}`} className="secondary-button">View</Link>
        <Link to={`/tasks/${task.id}/edit`} className="secondary-button">Edit</Link>
        <button className="danger-button" onClick={() => onDelete(task.id)}>Delete</button>
      </div>
    </article>
  );
}
