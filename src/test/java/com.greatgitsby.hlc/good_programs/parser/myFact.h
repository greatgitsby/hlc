{
   This computes the factorial of 9.
      Created by: 1569586
}

begin
   variable startNumber;
   variable result;
   variable i;

   {Initial Setup}
   startNumber := 9;
   i := startNumber - 1;
   result := startNumber;

   {Compute the factorial}
   while i > 1 do
   begin
      result := result * i;
      i := i - 1
   end;

   {Print it out}
   print "The factorial of ";
   print startNumber;
   print " is ";
   print result;
   print "\n"

end
{Answer should be 362880}
