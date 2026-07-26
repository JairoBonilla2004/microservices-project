import { describe, it, expect } from 'vitest'
import { render, screen } from '@testing-library/react'
import { AuthBackground } from '../../components/ui/AuthBackground'

describe('AuthBackground', () => {
  it('should render children', () => {
    render(
      <AuthBackground>
        <div data-testid="child">Test Content</div>
      </AuthBackground>
    )
    expect(screen.getByTestId('child')).toHaveTextContent('Test Content')
  })

  it('should render multiple children', () => {
    render(
      <AuthBackground>
        <span>First</span>
        <span>Second</span>
      </AuthBackground>
    )
    expect(screen.getByText('First')).toBeInTheDocument()
    expect(screen.getByText('Second')).toBeInTheDocument()
  })
})
