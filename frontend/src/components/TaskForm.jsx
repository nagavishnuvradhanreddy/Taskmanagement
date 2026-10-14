import { useState } from 'react';

const defaultForm = {
  title: '',
  description: '',
  status: 'TODO',
  priority: 'MEDIUM',
  dueDate: '',
};

export default function TaskForm({ initialValues = {}, onSubmit, submitLabel = 'Save Task' }) {
  const [form, setForm] = useState({
    ...defaultForm,
    ...initialValues,
    dueDate: initialValues.dueDate ? new Date(initialValues.dueDate).toISOString().slice(0, 16) : '',
  });

  const handleChange = (event) => {
    const { name, value } = event.target;
    setForm((current) => ({ ...current, [name]: value }));
  };

  const handleSubmit = (event) => {
    event.preventDefault();
    onSubmit({
      ...form,
      dueDate: form.dueDate ? new Date(form.dueDate).toISOString() : null,
    });
  };

  return (
    <form className="task-form" onSubmit={handleSubmit}>
      <div className="field-group">
        <label htmlFor="title">Title</label>
        <input id="title" name="title" value={form.title} onChange={handleChange} required />
      </div>

      <div className="field-group">
        <label htmlFor="description">Description</label>
        <textarea id="description" name="description" value={form.description} onChange={handleChange} rows="4" />
      </div>

      <div className="two-column-grid">
        <div className="field-group">
          <label htmlFor="status">Status</label>
          <select id="status" name="status" value={form.status} onChange={handleChange}>
            <option value="TODO">TODO</option>
            <option value="IN_PROGRESS">IN_PROGRESS</option>
            <option value="COMPLETED">COMPLETED</option>
          </select>
        </div>

        <div className="field-group">
          <label htmlFor="priority">Priority</label>
          <select id="priority" name="priority" value={form.priority} onChange={handleChange}>
            <option value="LOW">LOW</option>
            <option value="MEDIUM">MEDIUM</option>
            <option value="HIGH">HIGH</option>
          </select>
        </div>
      </div>

      <div className="field-group">
        <label htmlFor="dueDate">Due Date</label>
        <input id="dueDate" name="dueDate" type="datetime-local" value={form.dueDate} onChange={handleChange} />
      </div>

      <button type="submit" className="primary-button">{submitLabel}</button>
    </form>
  );
}
