import { describe, it, expect } from 'vitest'
import { render, screen } from '@testing-library/react'
import { Meter } from '../../../components/ui/Meter'

describe('Meter', () => {
  it('should render value/max and label', () => {
    render(<Meter value={3} max={10} label="Progreso" />)
    expect(screen.getByText('Progreso')).toBeInTheDocument()
    expect(screen.getByText('3/10')).toBeInTheDocument()
  })

  it('should render without label', () => {
    const { container } = render(<Meter value={5} max={10} />)
    expect(container.querySelector('.h-3')).toBeInTheDocument()
  })

  it('should render 0% when max is 0', () => {
    render(<Meter value={0} max={0} label="Vacío" />)
    expect(screen.getByText('0/0')).toBeInTheDocument()
  })

  it('should clamp to 100%', () => {
    render(<Meter value={200} max={100} label="Sobre" />)
    expect(screen.getByText('200/100')).toBeInTheDocument()
  })
})
