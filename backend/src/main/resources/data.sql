-- ── Puzzles ──────────────────────────────────────────────────────────────
-- CS Concepts
INSERT INTO puzzle (category, answer, hint) VALUES
  ('CONCEPT', 'BINARY SEARCH',      'An efficient lookup algorithm'),
  ('CONCEPT', 'BUBBLE SORT',        'A simple but slow sorting algorithm'),
  ('CONCEPT', 'LINKED LIST',        'A chain of nodes'),
  ('CONCEPT', 'HASH MAP',           'Key-value data structure'),
  ('CONCEPT', 'STACK OVERFLOW',     'Recursive call gone wrong'),
  ('CONCEPT', 'GARBAGE COLLECTION', 'Automatic memory management'),
  ('CONCEPT', 'DESIGN PATTERN',     'A reusable solution to a common problem'),
  ('CONCEPT', 'DEPENDENCY INJECTION','Inversion of control technique'),
  ('CONCEPT', 'RACE CONDITION',     'A concurrency bug'),
  ('CONCEPT', 'BIG O NOTATION',     'Describes algorithm complexity'),

-- Code Snippets
  ('SNIPPET', 'public int add(int a, int b) { return a + b; }',      'What does this method return?'),
  ('SNIPPET', 'public boolean isEmpty(String s) { return s.length() == 0; }', 'What does this check?'),
  ('SNIPPET', 'public int max(int a, int b) { return a > b ? a : b; }', 'What does this compare?'),
  ('SNIPPET', 'public String reverse(String s) { return new StringBuilder(s).reverse().toString(); }', 'What transformation happens here?'),
  ('SNIPPET', 'public boolean isPrime(int n) { return n > 1 && n % 2 != 0; }', 'What does this test?');
