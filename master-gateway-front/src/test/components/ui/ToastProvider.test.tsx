import { describe, it, expect, vi } from 'vitest'
import { render, screen, fireEvent, act } from '@testing-library/react'
import { ToastProvider, useToast } from '../../../components/ui/ToastProvider'

function TestButton() {
  const { showToast } = useToast()
  return (
    <div>
      <button onClick={() => showToast('¡Guardado!', 'success')}>Success</button>
      <button onClick={() => showToast('Error!', 'error')}>Error</button>
      <button onClick={() => showToast('Info')}>Info</button>
    </div>
  )
}

describe('ToastProvider', () => {
  beforeEach(() => { vi.useFakeTimers() })
  afterEach(() => { vi.useRealTimers() })

  it('should show success toast', () => {
    render(
      <ToastProvider>
        <TestButton />
      </ToastProvider>,
    )
    fireEvent.click(screen.getByText('Success'))
    expect(screen.getByText('¡Guardado!')).toBeInTheDocument()
  })

  it('should show error toast', () => {
    render(
      <ToastProvider>
        <TestButton />
      </ToastProvider>,
    )
    fireEvent.click(screen.getByText('Error'))
    expect(screen.getByText('Error!')).toBeInTheDocument()
  })

  it('should show info toast', () => {
    render(
      <ToastProvider>
        <TestButton />
      </ToastProvider>,
    )
    fireEvent.click(screen.getByRole('button', { name: 'Info' }))
    expect(screen.getAllByText('Info')).toHaveLength(2)
  })

  it('should dismiss toast after 4 seconds', () => {
    render(
      <ToastProvider>
        <TestButton />
      </ToastProvider>,
    )
    fireEvent.click(screen.getByText('Success'))
    expect(screen.getByText('¡Guardado!')).toBeInTheDocument()

    act(() => { vi.advanceTimersByTime(4000) })
    expect(screen.queryByText('¡Guardado!')).not.toBeInTheDocument()
  })

  it('should dismiss toast on close button click', () => {
    render(
      <ToastProvider>
        <TestButton />
      </ToastProvider>,
    )
    fireEvent.click(screen.getByText('Success'))
    const closeBtn = screen.getByRole('button', { name: '' })
    fireEvent.click(closeBtn)
    expect(screen.queryByText('¡Guardado!')).not.toBeInTheDocument()
  })

  it('useToast should throw outside provider', () => {
    expect(() => render(<TestButton />)).toThrow('useToast debe usarse dentro de ToastProvider')
  })
})
