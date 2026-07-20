export function BrandMark({ size = 28 }: { size?: number }) {
  return (
    <svg width={size} height={size} viewBox="0 0 32 32" fill="none" xmlns="http://www.w3.org/2000/svg">
      <defs>
        <linearGradient id="brandmark-grad" x1="0" y1="0" x2="32" y2="32" gradientUnits="userSpaceOnUse">
          <stop stopColor="#818cf8" />
          <stop offset="1" stopColor="#4338ca" />
        </linearGradient>
      </defs>
      <path
        d="M16 2 L28 7 V15 C28 22.5 22.9 28.6 16 30 C9.1 28.6 4 22.5 4 15 V7 L16 2 Z"
        fill="url(#brandmark-grad)"
      />
      <path
        d="M11 15.5 L14.5 19 L21.5 12"
        stroke="white"
        strokeWidth="2.4"
        strokeLinecap="round"
        strokeLinejoin="round"
        fill="none"
      />
    </svg>
  )
}
