// This file is part of www.nand2tetris.org
// and the book "The Elements of Computing Systems"
// by Nisan and Schocken, MIT Press.
// File name: projects/04/Fill.asm

// Runs an infinite loop that listens to the keyboard input.
// When a key is pressed (any key), the program blackens the screen
// by writing 'black' in every pixel;
// the screen should remain fully black as long as the key is pressed. 
// When no key is pressed, the program clears the screen by writing
// 'white' in every pixel;
// the screen should remain fully clear as long as no key is pressed.

(INIT)
	// Sets the counter at 1 to 8192 (32*256 16 bit pixels in the screen)
	@8192
	D=A
	@1
	M=D
(LOOP)
	// Subtracts one from the index
	@1
	M=M-1
	D=M
	
	// If the index is less than 0, reinitialize it to 8192
	@INIT
	D;JLT
	
	// If Memory @KBD is 0 (no key pressed) go to WHITE
	@KBD
	D=M
	@WHITE
	D;JEQ
	
	// Else go to BLACK
	@BLACK
	0;JMP
(BLACK)
	// Get the address of SCREEN
	@SCREEN
	D=A
	
	// add the address of SCREEN to the index
	@1
	A=M+D
	M=-1 // sets the 16 bit to -1 meaning 1111 1111 1111 1111 (every 16 pixels is black)
	
	// Go to LOOP
	@LOOP
	0;JMP
(WHITE)
	// Get the address of SCREEN
	@SCREEN
	D=A
	
	// Add the addredd of SCREEN to the index
	@1
	A=M+D
	M=0 // sets the 16 bit interger to 0 (white)
	
	// Go to LOOP
	@LOOP
	0;JMP
	
