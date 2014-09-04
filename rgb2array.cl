kernel void
convert_i_rgb_i_array(read_only image2d_t src, write_only global float* matrix, int  wdim) {
        int gx = get_global_id(0);
        int gy = get_global_id(1);

        if ( (gx < get_image_width(src)) & (gy < get_image_height(src)) ) {
                const sampler_t smp = CLK_NORMALIZED_COORDS_FALSE | CLK_ADDRESS_CLAMP_TO_EDGE | CLK_FILTER_NEAREST;
                int2 pos = { gx, gy };
                float4 pixel = read_imagef(src, smp, pos);
                // If the image is of type BGRA, UNormInt8, the pixel is in the form :
        		// (float4)(red, green, blue, alpha) with each component beinge [0.0f; 1.0f] interval.
                /* convert image rgb to image grey ignoring alpha */
                float g = pixel.s0 * 0.299f + pixel.s1 * 0.587f + pixel.s2 * 0.114f;
			    // Write the matrix to device memory each 
   				// thread writes one element
				matrix[gy * wdim + gx] = g*255;
          
        }
}
