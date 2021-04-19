/****************************************
 *                                      *
 *  AUTO-GENERATED, DO NOT MODIFY       *
 *                                      *
 *  FILE: n_fact                        *
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
	ldr r4, =10
	str r4, [fp, #-4]
	ldr r4, =1
	str r4, [fp, #-8]
	ldr r4, =1
	str r4, [fp, #-12]

.balign 4
begin_1:
	ldr r4, [fp, #-4]
	ldr r5, [fp, #-12]
	cmp r4, r5
	beq end_1
	ldr r5, [fp, #-12]
	ldr r6, =1
	add r4, r5, r6
	str r4, [fp, #-12]
	ldr r5, [fp, #-8]
	ldr r6, [fp, #-12]
	mul r4, r5, r6
	str r4, [fp, #-8]
	b begin_1

.balign 4
end_1:
	ldr r0, =ifmt
	ldr r4, [fp, #-4]
	mov r1, r4
	bl printf
	ldr r0, =sfmt
	ldr r4, =str_2
	mov r1, r4
	bl printf
	ldr r0, =ifmt
	ldr r4, [fp, #-8]
	mov r1, r4
	bl printf
	ldr r0, =sfmt
	ldr r4, =str_3
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
str_2:
	.asciz "! = "

.balign 4
str_3:
	.asciz "\n"

