import { describe, it, expect } from 'vitest'
import { render, screen } from '@testing-library/react'
import { EmptyState } from '../../components/ui/EmptyState'

describe('EmptyState', () => {
  it('should render title', () => {
    render(<EmptyState title="No data" />)
    expect(screen.getByText('No data')).toBeInTheDocument()
  })

  it('should render description', () => {
    render(<EmptyState title="Empty" description="Nothing to show here" />)
    expect(screen.getByText('Nothing to show here')).toBeInTheDocument()
  })

  it('should render action', () => {
    render(<EmptyState title="Empty" action={<button>Retry</button>} />)
    expect(screen.getByText('Retry')).toBeInTheDocument()
  })

  it('should render custom icon', () => {
    render(<EmptyState title="Empty" icon={<span data-testid="custom-icon">+</span>} />)
    expect(screen.getByTestId('custom-icon')).toBeInTheDocument()
  })

  it('should render default Inbox icon when no icon provided', () => {
    const { container } = render(<EmptyState title="Empty" />)
    const inboxIcon = container.querySelector('svg')
    expect(inboxIcon).toBeInTheDocument()
  })
})
