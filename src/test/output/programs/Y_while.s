/****************************************
 *                                      *
 *  AUTO-GENERATED, DO NOT MODIFY       *
 *                                      *
 *  FILE: while                         *
 *  DATE: Sun Apr 18 21:19:23 PDT 2021  *
 *                                      *
 ****************************************/

/*************
 *           *
 * EXTERNALS *
 *           *
 *************/
.extern printf
.extern __aeabi_idiv

/***************
 *             *
 * ENTRY POINT *
 *             *
 ***************/
.global main

/********
 *      *
 * MAIN *
 *      *
 ********/
.text
.balign 4
main:
	push { lr }
	mov fp, sp
	add sp, sp, #-4
	ldr r4, =0
	str r4, [fp, #-4]

.balign 4
begin_1:
	ldr r4, [fp, #-4]
	ldr r5, =30
	cmp r4, r5
	bge end_1
	ldr r0, =ifmt
	ldr r4, [fp, #-4]
	mov r1, r4
	bl printf
	ldr r0, =sfmt
	ldr r4, =str_2
	mov r1, r4
	bl printf
	ldr r5, [fp, #-4]
	ldr r6, =1
	add r4, r5, r6
	str r4, [fp, #-4]
	b begin_1

.balign 4
end_1:

.text
.balign 4
quit:
	mov sp, fp
	pop { lr }
	mov r0, #0
	bx lr

/********************
 *                  *
 * STRING CONSTANTS *
 *                  *
 ********************/
.data
.balign 4
ifmt:
	.asciz "%d"

.balign 4
sfmt:
	.asciz "%s"

.balign 4
str_2:
	.asciz "\n"

