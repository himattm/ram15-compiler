
class Fac():

	def ComputeFac(self, num):
		if (num < 1):
			num_aux = 1
		else:
			num_aux = (num * self.ComputeFac((num - 1)))

		return num_aux
if __name__ == '__main__':
	print Fac().ComputeFac(10)

