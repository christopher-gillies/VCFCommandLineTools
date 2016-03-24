package org.kidneyomics.referenceseq;

public class NucleotideMatcher {
	public static boolean matches(char A, char B) {
		

		
		if(A == B) {
			return true;
		} else {
			//swap if A is not ACTG or U
			switch(A) {
			case 'A':
			case 'C':
			case 'G':
			case 'T':
				break;
				default:
					//swap
					char tmp = A;
					A = B;
					B = tmp;
			}
		}
		
		switch(B) {
		case 'U':
			if(A == 'T') {
				return true;
			} else {
				return false;
			}
		case 'R':
			if(A == 'A' || A == 'G') {
				return true;
			} else {
				return false;
			}
		case 'Y':
			if(A == 'C' || A == 'T') {
				return true;
			} else {
				return false;
			}
		case 'S':
			if(A == 'G' || A == 'C') {
				return true;
			} else {
				return false;
			}
		case 'W':
			if(A == 'A' || A == 'T') {
				return true;
			} else {
				return false;
			}
		case 'K':
			if(A == 'G' || A == 'T') {
				return true;
			} else {
				return false;
			}
		case 'M':
			if(A == 'A' || A == 'C') {
				return true;
			} else {
				return false;
			}
		case 'B':
			if(A == 'C' || A == 'G' || A == 'T') {
				return true;
			} else {
				return false;
			}
		case 'D':
			if(A == 'A' || A == 'G' || A == 'T') {
				return true;
			} else {
				return false;
			}
		case 'H':
			if(A == 'A' || A == 'C' || A == 'T') {
				return true;
			} else {
				return false;
			}
		case 'V':
			if(A == 'A' || A == 'C' || A == 'G') {
				return true;
			} else {
				return false;
			}
		case 'N':
			if(A == '.' || A == '-') {
				return false;
			} else {
				return true;
			}
			default: 
				return false;
		}
		
		
	}
}
