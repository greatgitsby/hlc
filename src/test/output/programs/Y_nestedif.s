/****************************************
 *                                      *
 *  AUTO-GENERATED, DO NOT MODIFY       *
 *                                      *
 *  FILE: nestedif                      *
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
	ldr r4, =10
	str r4, [fp, #-4]
	ldr r4, [fp, #-4]
	ldr r5, =30
	cmp r4, r5
	bge end_1
	ldr r4, [fp, #-4]
	ldr r5, =10
	cmp r4, r5
	bge end_2
	ldr r0, =sfmt
	ldr r4, =str_2
	mov r1, r4
	bl printf
	b begin_2

.balign 4
end_2:
	ldr r0, =sfmt
	ldr r4, =str_3
	mov r1, r4
	bl printf

.balign 4
begin_2:
	b begin_1

.balign 4
end_1:
	ldr r0, =sfmt
	ldr r4, =str_4
	mov r1, r4
	bl printf

.balign 4
begin_1:

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
str_2:
	.asciz "oops, incorrect!\n"

.balign 4
sfmt:
	.asciz "%s"

.balign 4
str_3:
	.asciz "yup, correct!\n"

.balign 4
str_4:
	.asciz "nope, incorrect!\n"

