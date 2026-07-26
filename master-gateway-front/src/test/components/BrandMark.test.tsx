import { describe, it, expect } from 'vitest'
import { render } from '@testing-library/react'
import { BrandMark } from '../../components/ui/BrandMark'

describe('BrandMark', () => {
  it('should render with default size', () => {
    const { container } = render(<BrandMark />)
    const svg = container.querySelector('svg')
    expect(svg).toBeInTheDocument()
    expect(svg).toHaveAttribute('width', '28')
    expect(svg).toHaveAttribute('height', '28')
  })

  it('should render with custom size', () => {
    const { container } = render(<BrandMark size={48} />)
    const svg = container.querySelector('svg')
    expect(svg).toHaveAttribute('width', '48')
    expect(svg).toHaveAttribute('height', '48')
  })
})
