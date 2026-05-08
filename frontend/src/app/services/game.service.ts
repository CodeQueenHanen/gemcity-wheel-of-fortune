import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Observable, of, throwError } from 'rxjs';
import { catchError, delay } from 'rxjs/operators';
import { Puzzle, WheelSegment, GuessResult } from '../models/game.models';

const WHEEL_SEGMENTS: WheelSegment[] = [
  { type: 'POINTS', points: 100,  label: '100',        color: '#1a6b3a' },
  { type: 'POINTS', points: 250,  label: '250',        color: '#1a4a6b' },
  { type: 'POINTS', points: 500,  label: '500',        color: '#4a1a6b' },
  { type: 'POINTS', points: 750,  label: '750',        color: '#6b4a1a' },
  { type: 'POINTS', points: 100,  label: '100',        color: '#1a6b3a' },
  { type: 'BANKRUPT', points: 0,  label: 'BANKRUPT',   color: '#6b1a1a' },
  { type: 'POINTS', points: 250,  label: '250',        color: '#1a4a6b' },
  { type: 'FREE_VOWEL', points: 0, label: 'FREE VOWEL', color: '#6b3a1a' },
  { type: 'POINTS', points: 500,  label: '500',        color: '#4a1a6b' },
  { type: 'POINTS', points: 750,  label: '750',        color: '#6b4a1a' },
  { type: 'BANKRUPT', points: 0,  label: 'BANKRUPT',   color: '#6b1a1a' },
  { type: 'POINTS', points: 1000, label: '1000',       color: '#006b4a' },
];

const MOCK_PUZZLES: Puzzle[] = [
  {
    id: 1,
    category: 'CONCEPT',
    hint: 'A data structure',
    totalLetters: 11,
    visiblePattern: ['_','I','_','A','_','Y',' ','_','E','A','_','C','_'],
    solvedPositions: Array(13).fill(false)
  },
  {
    id: 2,
    category: 'SNIPPET',
    hint: 'What does this method do?',
    totalLetters: 9,
    visiblePattern: ['_','_','_',' ','_','_','_',' ','(','_','_','_',' ','_',')',' ','{',' ','_','_','_','_','_','_',' ','_',';',' ','}'],
    solvedPositions: Array(29).fill(false)
  },
  {
    id: 3,
    category: 'CONCEPT',
    hint: 'A sorting algorithm',
    totalLetters: 10,
    visiblePattern: ['_','_','_','_','_','_',' ','_','_','_','_','_','_'],
    solvedPositions: Array(13).fill(false)
  }
];

@Injectable({ providedIn: 'root' })
export class GameService {
  private http = inject(HttpClient);
  private apiBase = '/api';

  // Toggle this to false once Java backend is running
  private useMock = false;

  getRandomPuzzle(): Observable<Puzzle> {
    if (this.useMock) {
      const p = MOCK_PUZZLES[Math.floor(Math.random() * MOCK_PUZZLES.length)];
      return of({ ...p, solvedPositions: Array(p.visiblePattern.length).fill(false) }).pipe(delay(300));
    }
    return this.http.get<Puzzle>(`${this.apiBase}/puzzle/random`).pipe(
      catchError(this.handleError)
    );
  }

  spinWheel(): Observable<WheelSegment> {
    if (this.useMock) {
      const segment = WHEEL_SEGMENTS[Math.floor(Math.random() * WHEEL_SEGMENTS.length)];
      return of({ ...segment }).pipe(delay(200));
    }
    return this.http.get<WheelSegment>(`${this.apiBase}/wheel/spin`).pipe(
      catchError(this.handleError)
    );
  }

  guessLetter(puzzleId: number, letter: string, pointsPerHit: number, revealedLetters: Set<string> = new Set()): Observable<GuessResult> {
    if (this.useMock) {
      return of(this.mockGuess(puzzleId, letter, pointsPerHit)).pipe(delay(250));
    }
    return this.http.post<GuessResult>(`${this.apiBase}/game/guess`, {
      puzzleId,
      letter,
      pointsPerHit,
      revealedLetters: Array.from(revealedLetters)
    }).pipe(catchError(this.handleError));
  }

  solvePuzzle(puzzleId: number, attempt: string): Observable<GuessResult> {
    if (this.useMock) {
      return of(this.mockSolve(puzzleId, attempt)).pipe(delay(200));
    }
    return this.http.post<GuessResult>(`${this.apiBase}/game/solve`, {
      puzzleId, attempt
    }).pipe(catchError(this.handleError));
  }

  getWheelSegments(): WheelSegment[] {
    return WHEEL_SEGMENTS;
  }

  // ── Mock logic (mirrors what Java backend will do) ──────────────────────

  private mockState: Map<number, { answer: string; revealed: Set<string> }> = new Map([
    [1, { answer: 'BINARY SEARCH', revealed: new Set() }],
    [2, { answer: 'int add (int a) { return a; }', revealed: new Set() }],
    [3, { answer: 'BUBBLE SORT', revealed: new Set() }],
  ]);

  private mockGuess(puzzleId: number, letter: string, pointsPerHit: number): GuessResult {
    const state = this.mockState.get(puzzleId);
    const puzzle = MOCK_PUZZLES.find(p => p.id === puzzleId);
    if (!state || !puzzle) throw new Error('Puzzle not found');

    const upper = letter.toUpperCase();
    state.revealed.add(upper);

    const answer = state.answer.toUpperCase();
    const matchCount = [...answer].filter(c => c === upper).length;
    const correct = matchCount > 0;
    const pointsEarned = correct ? matchCount * pointsPerHit : 0;

    const updatedPattern = [...answer].map(c => {
      if (c === ' ') return ' ';
      if (['{','}','(',')','[',']',';','.',',',':','<','>','"',"'",'+','-','*','/','!','=','_'].includes(c)) return c;
      return state.revealed.has(c) ? c : '_';
    });

    const solvedPositions = updatedPattern.map((c, i) => c === answer[i] && c !== '_');
    const puzzleSolved = updatedPattern.join('') === answer;

    return { letter: upper, correct, matchCount, pointsEarned, updatedPattern, solvedPositions, puzzleSolved, newScore: 0 };
  }

  private mockSolve(puzzleId: number, attempt: string): GuessResult {
    const state = this.mockState.get(puzzleId);
    if (!state) throw new Error('Puzzle not found');
    const correct = attempt.toUpperCase().trim() === state.answer.toUpperCase().trim();
    const answer = state.answer.toUpperCase();
    const updatedPattern = correct ? [...answer] : (MOCK_PUZZLES.find(p=>p.id===puzzleId)?.visiblePattern ?? []);
    return {
      letter: '', correct, matchCount: 0, pointsEarned: correct ? 500 : 0,
      updatedPattern, solvedPositions: Array(updatedPattern.length).fill(correct),
      puzzleSolved: correct, newScore: 0
    };
  }

  private handleError(err: HttpErrorResponse): Observable<never> {
    console.error('API error', err);
    return throwError(() => new Error(err.message));
  }
}