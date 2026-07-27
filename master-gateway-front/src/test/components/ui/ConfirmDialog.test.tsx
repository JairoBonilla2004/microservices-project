import { describe, it, expect } from 'vitest'
import { render, screen, fireEvent } from '@testing-library/react'
import { ConfirmDialogProvider, useConfirm } from '../../../components/ui/ConfirmDialog'

function TestButton() {
  const confirm = useConfirm()
  return (
    <button onClick={() => confirm({ title: '¿Eliminar?', description: 'Esta acción es irreversible', variant: 'danger' })}>
      Abrir
    </button>
  )
}

describe('ConfirmDialogProvider', () => {
  it('should show dialog on confirm call', async () => {
    render(
      <ConfirmDialogProvider>
        <TestButton />
      </ConfirmDialogProvider>,
    )
    fireEvent.click(screen.getByText('Abrir'))
    expect(screen.getByText('¿Eliminar?')).toBeInTheDocument()
    expect(screen.getByText('Esta acción es irreversible')).toBeInTheDocument()
  })

  it('should close dialog when clicking Cancelar', () => {
    render(
      <ConfirmDialogProvider>
        <TestButton />
      </ConfirmDialogProvider>,
    )
    fireEvent.click(screen.getByText('Abrir'))
    fireEvent.click(screen.getByText('Cancelar'))
    expect(screen.queryByText('¿Eliminar?')).not.toBeInTheDocument()
  })

  it('should close dialog when clicking Confirmar', () => {
    render(
      <ConfirmDialogProvider>
        <TestButton />
      </ConfirmDialogProvider>,
    )
    fireEvent.click(screen.getByText('Abrir'))
    fireEvent.click(screen.getByText('Confirmar'))
    expect(screen.queryByText('¿Eliminar?')).not.toBeInTheDocument()
  })

  it('should show custom confirm label', () => {
    function CustomLabel() {
      const confirm = useConfirm()
      return <button onClick={() => confirm({ title: 'Test', confirmLabel: 'Sí, eliminar' })}>Abrir</button>
    }
    render(
      <ConfirmDialogProvider>
        <CustomLabel />
      </ConfirmDialogProvider>,
    )
    fireEvent.click(screen.getByText('Abrir'))
    expect(screen.getByText('Sí, eliminar')).toBeInTheDocument()
  })

  it('should render danger variant with red styles', () => {
    render(
      <ConfirmDialogProvider>
        <TestButton />
      </ConfirmDialogProvider>,
    )
    fireEvent.click(screen.getByText('Abrir'))
    const confirmBtn = screen.getByText('Confirmar')
    expect(confirmBtn.className).toContain('bg-red-600')
  })

  it('should render default variant without danger styles', () => {
    function DefaultTest() {
      const confirm = useConfirm()
      return <button onClick={() => confirm({ title: 'Info' })}>Info</button>
    }
    render(
      <ConfirmDialogProvider>
        <DefaultTest />
      </ConfirmDialogProvider>,
    )
    fireEvent.click(screen.getByText('Info'))
    const confirmBtn = screen.getByText('Confirmar')
    expect(confirmBtn.className).not.toContain('bg-red-600')
  })

  it('useConfirm should throw outside provider', () => {
    expect(() => render(<TestButton />)).toThrow('useConfirm debe usarse dentro de ConfirmDialogProvider')
  })
})
