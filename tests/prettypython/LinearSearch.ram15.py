
class LS():
	def __init__ (self):
		self.number = None
		self.size = None
	def Start(self, sz):
		aux01 = self.Init(sz)
		aux02 = self.Print()
		print 9999
		print self.Search(8)
		print self.Search(12)
		print self.Search(17)
		print self.Search(50)
		return 55
	def Print(self):
		j = 1
		while (j < self.size):
			print self.number[j]
			j += 1

		return 0
	def Search(self, num):
		j = 1
		ls01 = False
		ifound = 0
		while (j < self.size):
			aux01 = self.number[j]
			aux02 = (num + 1)
			if (aux01 < num):
				nt = 0
			else:
				if not(aux01 < aux02):
					nt = 0
				else:
					ls01 = True
					ifound = 1
					j = self.size



			j = (j + 1)

		return ifound
	def Init(self, sz):
		self.size = sz
		self.number = [None]*sz
		j = 1
		k = (self.size + 1)
		while (j < self.size):
			aux01 = (2 * j)
			aux02 = (k - 3)
			self.number[j] = (aux01 + aux02)
			j += 1
			k = (k - 1)

		return 0
if __name__ == '__main__':
	print LS().Start(10)

