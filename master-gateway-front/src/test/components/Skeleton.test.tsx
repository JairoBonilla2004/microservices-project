import { describe, it, expect } from 'vitest'
import { render } from '@testing-library/react'
import { Skeleton, TableSkeleton } from '../../components/ui/Skeleton'

describe('Skeleton', () => {
  it('should render with default classes', () => {
    const { container } = render(<Skeleton />)
    const div = container.firstChild as HTMLElement
    expect(div.className).toContain('animate-pulse')
    expect(div.className).toContain('rounded-md')
    expect(div.className).toContain('bg-slate-200/70')
  })

  it('should apply custom className', () => {
    const { container } = render(<Skeleton className="h-10 w-full" />)
    const div = container.firstChild as HTMLElement
    expect(div.className).toContain('h-10')
    expect(div.className).toContain('w-full')
  })
})

describe('TableSkeleton', () => {
  it('should render default number of rows and columns', () => {
    const { container } = render(<TableSkeleton />)
    const rows = container.firstChild?.childNodes
    expect(rows).toHaveLength(5)
    const firstRow = rows?.[0]?.childNodes
    expect(firstRow).toHaveLength(4)
  })

  it('should render custom number of rows and cols', () => {
    const { container } = render(<TableSkeleton rows={3} cols={2} />)
    const rows = container.firstChild?.childNodes
    expect(rows).toHaveLength(3)
    const firstRow = rows?.[0]?.childNodes
    expect(firstRow).toHaveLength(2)
  })
})
