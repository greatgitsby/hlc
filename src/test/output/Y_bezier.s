/****************************************
 *                                      *
 *  AUTO-GENERATED, DO NOT MODIFY       *
 *                                      *
 *  FILE: bezier                        *
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
	add sp, sp, #-4
	add sp, sp, #-4
	ldr r4, =0
	str r4, [fp, #-4]
	ldr r5, =0
	ldr r6, =15
	sub r4, r5, r6
	str r4, [fp, #-8]
	ldr r4, =10
	str r4, [fp, #-12]
	ldr r4, =1
	ldr r5, =10
	mov r0, r4
	mov r1, r5
	bl __aeabi_idiv
	mov r6, r0
	str r6, [fp, #-16]
	ldr r0, =sfmt
	ldr r4, =str_2
	mov r1, r4
	bl printf
	ldr r0, =ifmt
	ldr r4, [fp, #-16]
	mov r1, r4
	bl printf
	ldr r0, =sfmt
	ldr r4, =str_3
	mov r1, r4
	bl printf
	ldr r5, =1
	ldr r6, [fp, #-16]
	sub r4, r5, r6
	ldr r6, [fp, #-4]
	mul r5, r6, r4
	ldr r6, [fp, #-8]
	ldr r7, [fp, #-16]
	mul r4, r6, r7
	add r6, r5, r4
	str r6, [fp, #-20]
	ldr r5, =1
	ldr r6, [fp, #-16]
	sub r4, r5, r6
	ldr r6, [fp, #-8]
	mul r5, r6, r4
	ldr r6, [fp, #-12]
	ldr r7, [fp, #-16]
	mul r4, r6, r7
	add r6, r5, r4
	str r6, [fp, #-24]
	ldr r5, =1
	ldr r6, [fp, #-16]
	sub r4, r5, r6
	ldr r6, [fp, #-20]
	mul r5, r6, r4
	ldr r6, [fp, #-24]
	ldr r7, [fp, #-16]
	mul r4, r6, r7
	add r6, r5, r4
	str r6, [fp, #-28]
	ldr r0, =sfmt
	ldr r4, =str_4
	mov r1, r4
	bl printf
	ldr r0, =ifmt
	ldr r4, [fp, #-28]
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
 * STRING CONSTANTS *
 ********************/
.data
.balign 4
str_4:
	.asciz "Interpolated value: "

.balign 4
ifmt:
	.asciz "%d"

.balign 4
sfmt:
	.asciz "%s"

.balign 4
str_2:
	.asciz "Distance: "

.balign 4
str_3:
	.asciz "\n"

