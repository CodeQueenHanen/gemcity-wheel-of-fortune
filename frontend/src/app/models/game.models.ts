export type PuzzleCategory = 'CONCEPT' | 'SNIPPET';

export type WheelSegmentType = 'POINTS' | 'BANKRUPT' | 'FREE_VOWEL';

export interface Puzzle {
  id: number;
  category: PuzzleCategory;
  hint: string;
  totalLetters: number;
  visiblePattern: string[];   // array of chars: letter, '_', or revealed char
  solvedPositions: boolean[]; // which positions are revealed
}

export interface WheelSegment {
  type: WheelSegmentType;
  points: number;             // 0 for BANKRUPT / FREE_VOWEL
  label: string;
  color: string;
}

export interface GuessResult {
  letter: string;
  correct: boolean;
  matchCount: number;
  pointsEarned: number;
  updatedPattern: string[];
  solvedPositions: boolean[];
  puzzleSolved: boolean;
  newScore: number;
}

export interface GameState {
  puzzle: Puzzle;
  score: number;
  roundScore: number;         // score earned this spin before banking
  guessedLetters: string[];
  currentSegment: WheelSegment | null;
  phase: GamePhase;
  message: string;
}

export type GamePhase =
  | 'SPIN'            // waiting to spin
  | 'GUESS'           // player picks a letter
  | 'RESULT'          // showing feedback for last guess
  | 'SOLVE_ATTEMPT'   // player typed full solution
  | 'SOLVED'          // puzzle complete
  | 'BANKRUPT';       // just went bankrupt
