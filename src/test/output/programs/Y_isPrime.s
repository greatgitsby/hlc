/****************************************
 *                                      *
 *  AUTO-GENERATED, DO NOT MODIFY       *
 *                                      *
 *  FILE: isPrime                       *
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
	add sp, sp, #-4
	add sp, sp, #-4
	add sp, sp, #-4
	add sp, sp, #-4
	add sp, sp, #-4
	ldr r4, =31
	str r4, [fp, #-4]
	ldr r5, [fp, #-4]
	ldr r6, =1
	sub r4, r5, r6
	str r4, [fp, #-8]
	ldr r4, =1
	str r4, [fp, #-20]
	ldr r4, =2
	str r4, [fp, #-24]
	ldr r4, [fp, #-4]
	ldr r5, =1
	cmp r4, r5
	bgt end_1
	ldr r4, =0
	str r4, [fp, #-20]
	b begin_1

.balign 4
end_1:

.balign 4
begin_2:
	ldr r4, [fp, #-24]
	ldr r5, [fp, #-4]
	cmp r4, r5
	bge end_2
	ldr r4, [fp, #-4]
	ldr r5, [fp, #-24]
	mov r0, r4
	mov r1, r5
	bl __aeabi_idiv
	mov r6, r0
	str r6, [fp, #-12]
	ldr r4, [fp, #-8]
	ldr r5, [fp, #-24]
	mov r0, r4
	mov r1, r5
	bl __aeabi_idiv
	mov r6, r0
	str r6, [fp, #-16]
	ldr r4, [fp, #-12]
	ldr r5, [fp, #-16]
	cmp r4, r5
	beq end_3
	ldr r4, =0
	str r4, [fp, #-20]
	ldr r4, [fp, #-4]
	str r4, [fp, #-24]
	b begin_3

.balign 4
end_3:

.balign 4
begin_3:
	ldr r5, [fp, #-24]
	ldr r6, =1
	add r4, r5, r6
	str r4, [fp, #-24]
	b begin_2

.balign 4
end_2:

.balign 4
begin_1:
	ldr r0, =sfmt
	ldr r4, =str_2
	mov r1, r4
	bl printf
	ldr r0, =ifmt
	ldr r4, [fp, #-4]
	mov r1, r4
	bl printf
	ldr r0, =sfmt
	ldr r4, =str_3
	mov r1, r4
	bl printf
	ldr r4, [fp, #-20]
	ldr r5, =0
	cmp r4, r5
	bne end_4
	ldr r0, =sfmt
	ldr r4, =str_4
	mov r1, r4
	bl printf
	b begin_4

.balign 4
end_4:

.balign 4
begin_4:
	ldr r0, =sfmt
	ldr r4, =str_5
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
str_3:
	.asciz " is "

.balign 4
str_4:
	.asciz "not "

.balign 4
str_5:
	.asciz "prime."

.balign 4
str_2:
	.asciz "The number "

