import { describe, it, expect } from 'vitest'
import { render, screen } from '@testing-library/react'
import { Card, CardHeader, CardBody } from '../../components/ui/Card'

describe('Card', () => {
  it('should render children', () => {
    render(<Card>Content</Card>)
    expect(screen.getByText('Content')).toBeInTheDocument()
  })

  it('should apply custom className', () => {
    render(<Card className="custom-class">Content</Card>)
    expect(screen.getByText('Content').className).toContain('custom-class')
  })
})

describe('CardHeader', () => {
  it('should render title', () => {
    render(<CardHeader title="Card Title" />)
    expect(screen.getByText('Card Title')).toBeInTheDocument()
  })

  it('should render description', () => {
    render(<CardHeader title="Title" description="A description" />)
    expect(screen.getByText('A description')).toBeInTheDocument()
  })

  it('should render action', () => {
    render(<CardHeader title="Title" action={<button>Action</button>} />)
    expect(screen.getByText('Action')).toBeInTheDocument()
  })
})

describe('CardBody', () => {
  it('should render children', () => {
    render(<CardBody>Body content</CardBody>)
    expect(screen.getByText('Body content')).toBeInTheDocument()
  })
})
