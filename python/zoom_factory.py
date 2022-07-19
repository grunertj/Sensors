import matplotlib.pyplot as plt
# https://stackoverflow.com/questions/11551049/matplotlib-plot-zooming-with-scroll-wheel

def zoom_factory(ax,base_scale = 2.):
    " https://stackoverflow.com/questions/11551049/matplotlib-plot-zooming-with-scroll-wheel "
    def zoom_fun(event):
        xdata = event.xdata # get event x location
        ydata = event.ydata # get event y location
        # get the current x and y limits
        cur_xlim = ax.get_xlim()
        cur_ylim = ax.get_ylim()
        cur_xrange = (cur_xlim[1] - cur_xlim[0])*.5
        cur_yrange = (cur_ylim[1] - cur_ylim[0])*.5
        relx = (cur_xlim[1]-xdata)/(cur_xlim[1]-cur_xlim[0])
        rely = (cur_ylim[1]-ydata)/(cur_ylim[1]-cur_ylim[0])

        if event.button == 'up':
            # deal with zoom in
            scale_factor = 1/base_scale
        elif event.button == 'down':
            # deal with zoom out
            scale_factor = base_scale
        else:
            # deal with something that should never happen
            scale_factor = 1
            print(event.button)
        # set new limits
        new_width = (cur_xlim[1]-cur_ylim[0])*scale_factor
        new_height= (cur_xlim[1]-cur_ylim[0])*scale_factor

        ax.figure.canvas.toolbar.push_current()

        # 1.)
        # ax.set_xlim([xdata - cur_xrange*scale_factor, xdata + cur_xrange*scale_factor])
        # ax.set_ylim([ydata - cur_yrange*scale_factor, ydata + cur_yrange*scale_factor])

        # 2.)
        ax.set_xlim([xdata - (xdata-cur_xlim[0]) / scale_factor, xdata + (cur_xlim[1]-xdata) / scale_factor])
        ax.set_ylim([ydata - (ydata-cur_ylim[0]) / scale_factor, ydata + (cur_ylim[1]-ydata) / scale_factor])

        # 3.)
        # ax.set_xlim([xdata-new_width*(1-relx),xdata+new_width*(relx)])
        # ax.set_ylim([ydata-new_width*(1-rely),ydata+new_width*(rely)])

        plt.draw() # force re-draw

    fig = ax.get_figure() # get the figure of interest
    # attach the call back
    fig.canvas.mpl_connect('scroll_event',zoom_fun)

    #return the function
    return zoom_fun
