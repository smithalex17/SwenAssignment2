package tinyboycov.core;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collections;
import java.util.List;

import org.eclipse.jdt.annotation.Nullable;

import tinyboy.core.ControlPad;
import tinyboy.core.TinyBoyInputSequence;
import tinyboy.util.AutomatedTester;

/**
 * The TinyBoy Input Generator is responsible for generating and refining inputs
 * to try and ensure that sufficient branch coverage is obtained.
 *
 * @author David J. Pearce
 *
 */
public class TinyBoyInputGenerator implements AutomatedTester.InputGenerator<TinyBoyInputSequence> {
	/**
	 * Represents the number of buttons on the control pad.
	 */
	private final static int NUM_BUTTONS = ControlPad.Button.values().length;
	/**
	 * Current batch being processed
	 */
	private ArrayList<TinyBoyInputSequence> worklist = new ArrayList<>();
	
	private ArrayList<sequence> sequenceList = new ArrayList<>();

	/**
	 * Create new input generator for the TinyBoy simulation.
	 */
	public TinyBoyInputGenerator() {
		// FIXME: this is a very simplistic and poor implementation. However, it
		// illustrates how to create input sequences!
		ControlPad.Button left = ControlPad.Button.LEFT;
		ControlPad.Button right = ControlPad.Button.RIGHT;
		ControlPad.Button up = ControlPad.Button.UP;
		ControlPad.Button down = ControlPad.Button.DOWN;	
		
		boolean brute = true;
		if(brute) {
			for(ControlPad.Button bFirst : ControlPad.Button.values()) {
				for(ControlPad.Button bSecond : ControlPad.Button.values()) {
					for(ControlPad.Button bThird : ControlPad.Button.values()) {
						for(ControlPad.Button bForth : ControlPad.Button.values()) {
							for(ControlPad.Button bFifth : ControlPad.Button.values()) {
								for(ControlPad.Button bSixth : ControlPad.Button.values()) {
									for(ControlPad.Button bSeventh : ControlPad.Button.values()) {
										for(ControlPad.Button bEighth : ControlPad.Button.values()) {
											for(ControlPad.Button bNinth : ControlPad.Button.values()) {
												for(ControlPad.Button bTenth : ControlPad.Button.values()) {
													this.worklist.add(new TinyBoyInputSequence(bFirst,bSecond,bThird, bForth, bFifth, bSixth, bSeventh, bEighth, bNinth, bTenth));
												}
											}
										}
									}
								}
							}
						}		
					}
				}
			}
		} else {
			
			for(ControlPad.Button values : ControlPad.Button.values()) {
				this.worklist.add(new TinyBoyInputSequence(values));
			}
			
			/*
			this.worklist.add(new TinyBoyInputSequence(up, right, down, left, up, right, left, down));
			for(ControlPad.Button bFirst : ControlPad.Button.values()) {
				for(ControlPad.Button bSecond : ControlPad.Button.values()) {
					for(ControlPad.Button bThird : ControlPad.Button.values()) {
						this.worklist.add(new TinyBoyInputSequence(bFirst,bSecond,bThird));
					}
				}
			}
			*/
		}
			
		
		
	}

	@Override
	public boolean hasMore() {
		return this.worklist.size() > 0;
	}

	@Override
	public @Nullable TinyBoyInputSequence generate() {
		if (!this.worklist.isEmpty()) {
			// remove last item from worklist
			
			System.out.println("gen worklist: " + this.worklist.toString());
			return this.worklist.remove(this.worklist.size() - 1);
		}
		return null;
	}

	/**
	 * A record returned from the fuzzer indicating the coverage and final state
	 * obtained for a given input sequence.
	 */
	@Override
	public void record(TinyBoyInputSequence input, BitSet coverage, byte[] state) {
		// NOTE: this method is called when fuzzing has finished for a given input. It
		// produces three potentially useful items: firstly, the input sequence that was
		// used for fuzzing; second, the set of instructions which were covered when
		// executing that sequence; finally, the complete state of the machine's RAM at
		// the end of the run.
		//
		// At this point, you will want to use the feedback gained from fuzzing to help
		// prune the space of inputs to try next. A few helper methods are given below,
		// but you will need to write a lot more.
		
		int pLen = input.length();
		String s_in ;
		int s_cov;
		String s_state;
		String bAll = "";	
			
			//System.out.println("byte: " + bAll);
			
			System.out.println("Input: " + input.toString());
			System.out.println("Coverage: " + coverage.length());
			System.out.println("State: " + state.toString());
			//System.out.println("Input: " + input.next());
			if (this.worklist.isEmpty()){
				s_in = input.toString();
				s_cov = coverage.length();
				s_state = state.toString();
				sequenceList.add(new sequence(s_in, s_cov, s_state));
				sequence max = new sequence();
				int maxCoverage = 0;
				for(sequence s : sequenceList) {
					if(s.coverage > maxCoverage) {
						max = s;
						maxCoverage = s.coverage;
					}
				}
				
				//System.out.println(max.input);
				
				

			}else {
				s_in = input.toString();
				s_cov = coverage.length();
				s_state = state.toString();
				sequenceList.add(new sequence(s_in, s_cov, s_state));
				
				System.out.println("Input: " + input.toString());
				System.out.println("Coverage: " + coverage.length());
				System.out.println("State: " + state.toString());
			}
		
		
		
		
		
		

		
	}

	/**
	 * Check whether a given input sequence is completely subsumed by another.
	 *
	 * @param lhs The one which may be subsumed.
	 * @param rhs The one which may be subsuming.
	 * @return True if lhs subsumed by rhs, false otherwise.
	 */
	public static boolean subsumedBy(BitSet lhs, BitSet rhs) {
		for (int i = lhs.nextSetBit(0); i >= 0; i = lhs.nextSetBit(i + 1)) {
			if (!rhs.get(i)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Reduce a given set of items to at most <code>n</code> inputs by randomly
	 * sampling.
	 *
	 * @param inputs List of inputs to sample from.
	 * @param n      Size of inputs after reduction.
	 */
	private static <T> void randomSample(List<T> inputs, int n) {
		// Randomly shuffle inputs
		Collections.shuffle(inputs);
		// Remove inputs until only n remain
		while (inputs.size() > n) {
			inputs.remove(inputs.size() - 1);
		}
	}
	
	public class sequence{
		public String input;
		public int coverage;
		public String state;
		
		sequence(){}
		
		sequence(String input, int coverage, String state){
			this.input = input;
			this.coverage = coverage;
			this.state = state;
		}
		
	}
	
}
