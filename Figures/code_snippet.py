from PIL import Image
img= Image.open(f'Figures/Figures_revised/PNG/Fig2.png')
# Calculate new height maintaining aspect ratio
width = 1500
aspect_ratio = img.height / img.width
new_height = int(width * aspect_ratio)
resized_img = img.resize((width, new_height), Image.LANCZOS)
resized_img.save(f'Figures/Figures_Updated/TIFF/Fig1.tif', dpi=(300, 300), format='TIFF',compression='tiff_lzw')
resized_img.save(f'Figures/Figures_Updated/PNG/Fig1.png', dpi=(300, 300), format='PNG')
