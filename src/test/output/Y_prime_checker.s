/****************************************
 *                                      *
 *  AUTO-GENERATED, DO NOT MODIFY       *
 *                                      *
 *  FILE: prime_checker                 *
 *  DATE: Sun Apr 18 21:01:26 PDT 2021  *
 *                                      *
 ****************************************/

/*************
 * EXTERNALS *
 *************/
.extern printf
.extern __aeabi_idiv

/***************
 * ENTRY POINT *
 ***************/
.global main

/********
 * MAIN *
 ********/
.text
.balign 4
main:
	push { lr }
	mov fp, sp
	add sp, sp, #-4
	add sp, sp, #-4
	add sp, sp, #-4
	add sp, sp, #-4
	add sp, sp, #-4
	ldr r4, =7919
	str r4, [fp, #-4]
	ldr r4, =2
	str r4, [fp, #-8]
	ldr r4, =0
	str r4, [fp, #-20]
	ldr r4, =4
	str r4, [fp, #-12]

.balign 4
begin_1:
	ldr r4, [fp, #-12]
	ldr r5, [fp, #-4]
	cmp r4, r5
	bgt end_1
	ldr r4, [fp, #-4]
	str r4, [fp, #-16]

.balign 4
begin_2:
	ldr r4, [fp, #-16]
	ldr r5, =0
	cmp r4, r5
	ble end_2
	ldr r5, [fp, #-16]
	ldr r6, [fp, #-8]
	sub r4, r5, r6
	str r4, [fp, #-16]
	b begin_2

.balign 4
end_2:
	ldr r4, [fp, #-16]
	ldr r5, =0
	cmp r4, r5
	bne end_3
	ldr r4, =1
	str r4, [fp, #-20]
	b begin_3

.balign 4
end_3:

.balign 4
begin_3:
	ldr r5, [fp, #-8]
	ldr r6, =1
	add r4, r5, r6
	str r4, [fp, #-8]
	ldr r5, [fp, #-8]
	mul r4, r5, r5
	str r4, [fp, #-12]
	b begin_1

.balign 4
end_1:
	ldr r0, =ifmt
	ldr r4, [fp, #-4]
	mov r1, r4
	bl printf
	ldr r4, [fp, #-20]
	ldr r5, =0
	cmp r4, r5
	bne end_4
	ldr r0, =sfmt
	ldr r4, =str_2
	mov r1, r4
	bl printf
	b begin_4

.balign 4
end_4:
	ldr r0, =sfmt
	ldr r4, =str_3
	mov r1, r4
	bl printf

.balign 4
begin_4:
	ldr r0, =sfmt
	ldr r4, =str_4
	mov r1, r4
	bl printf

.text
.balign 4
quit:
	mov sp, fp
	pop { lr }
	mov r0, #0
	bx lr

/********************
 * STRING CONSTANTS *
 ********************/
.data
.balign 4
ifmt:
	.asciz "%d"

.balign 4
sfmt:
	.asciz "%s"

.balign 4
str_3:
	.asciz " is not prime"

.balign 4
str_4:
	.asciz "\n"

.balign 4
str_2:
	.asciz " is prime"

