// Sets the sum to 0
@2
M=0

// If the value at 0 and 1 = 0, go to END
@0
D=M
@END
D;JEQ

@1
D=M
@END
D;JEQ

(STEP)
	// If the value at 0 = 0, go to END
	@0
	D=M
	
	@END
	D;JEQ
	
	// Adds the value at 1 to the sum at 2
	@1
	D=M
	
	@2
	M=M+D
	
	// Subtracts 1 from the value at 0
	@0
	D=M-1
	M=D
	
	// Jumps to STEP
	@STEP
	0;JMP
(END)
	// Conventional way to end the program
	@END
	0;JMP

